package com.apiecommerce.tokoto.auth;

import com.apiecommerce.tokoto.config.JwtService;
import com.apiecommerce.tokoto.gender.Gender;
import com.apiecommerce.tokoto.gender.GenderRepository;
import com.apiecommerce.tokoto.location.district.District;
import com.apiecommerce.tokoto.location.district.DistrictService;
import com.apiecommerce.tokoto.location.province.Province;
import com.apiecommerce.tokoto.location.province.ProvinceService;
import com.apiecommerce.tokoto.location.regency.Regency;
import com.apiecommerce.tokoto.location.regency.RegencyService;
import com.apiecommerce.tokoto.location.village.Village;
import com.apiecommerce.tokoto.location.village.VillageService;
import com.apiecommerce.tokoto.storage.StorageService;
import com.apiecommerce.tokoto.token.Token;
import com.apiecommerce.tokoto.token.TokenRepository;
import com.apiecommerce.tokoto.token.TokenType;
import com.apiecommerce.tokoto.user.User;
import com.apiecommerce.tokoto.user.UserProjection;
import com.apiecommerce.tokoto.user.UserRepository;
import com.apiecommerce.tokoto.utils.ImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GenderRepository genderRepository;
    private final ProvinceService provinceService;
    private final RegencyService regencyService;
    private final DistrictService districtService;
    private final VillageService villageService;

    @Value("${server.host}")
    private String HOST;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access.name}")
    private String minioAccessName;

    @Value("${minio.access.secret}")
    private String minioAccessSecret;

    @Autowired
    private StorageService storageService;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }

        Gender gender = genderRepository.findByName(request.getGender().getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gender With Name: " + request.getGender().getName() + " Not Found!"));

        Province province = provinceService.findByName(request.getProvinceName().getName());
        Regency regency = regencyService.findByNames(province.getName(), request.getRegencyName().getName());
        District district = districtService.findDistrictByNames(province.getName(), regency.getName(), request.getDistrictName().getName());
        Village village = villageService.getVillageByDistrictAndNames(province.getName(), regency.getName(), district.getName(), request.getVillageName().getName());

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .gender(gender)
                .role(request.getRole())
                .province(province)
                .regency(regency)
                .district(district)
                .village(village)
                .build();

        setDefaultProfile(user);
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password", e);
        }
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserProjection findUserByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email dengan alamat: " + email + " Tidak dapat ditemukan"));

        return UserProjection.builder()
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phone(user.getPhone())
                .gender(user.getGender().getName())
                .province(user.getProvince().getName())
                .role(user.getRole())
                .regency(user.getRegency().getName())
                .district(user.getDistrict().getName())
                .village(user.getVillage().getName())
                .profileName(user.getProfileName())
                .build();
    }

    public List<UserProjection> findUserWithRegency(String provinceName, String regencyName) {
        Province province = provinceService.findByName(provinceName);
        Regency regency = regencyService.findByNames(province.getName(), regencyName);

        List<User> user = repository.findByProvinceNameAndRegencyName(province.getName(), regency.getName());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found in province: " + provinceName + " and regency: " + regencyName);
        }

        return user.stream()
                .map(users -> UserProjection.builder()
                        .email(users.getEmail())
                        .firstname(users.getFirstname())
                        .lastname(users.getLastname())
                        .phone(users.getPhone())
                        .province(users.getProvince().getName())
                        .regency(users.getRegency().getName())
                        .role(users.getRole())
                        .district(users.getDistrict().getName())
                        .village(users.getVillage().getName())
                        .profileName(users.getProfileName())
                        .build())
                .collect(Collectors.toList());
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void saveUserProfile(String email, MultipartFile file) {
        try {
            User user = repository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email dengan alamat: " + email + " Tidak dapat ditemukan"));

            List<String> allowedExtensions = Arrays.asList("PNG", "png", "JPG", "jpg", "JPEG", "jpeg", "webp");

            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
                if (!allowedExtensions.contains(extension)) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File extension not supported");
                }

                String hashedFileName = ImageUtils.hashFileName(originalFileName, file.getBytes());
                byte[] profileData = ImageUtils.compressImage(file.getBytes());
//                Path imagePath = Paths.get(uploadPath, hashedFileName);
//                Files.write(imagePath, profileData);
                storageService.uploadImageToMinio(file.getInputStream(), hashedFileName, file.getContentType());

                user.setProfileName(hashedFileName);
                user.setProfileType(file.getContentType());
                user.setProfileData(profileData);
                repository.save(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void setDefaultProfile(User user) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket("inventaris")
                    .object("default_profile.jpg")
                    .build();
            byte[] defaultProfile = minioClient.getObject(getObjectArgs).readAllBytes();
            user.setProfileName("default_profile.jpg");
            user.setProfileType(MediaType.IMAGE_JPEG_VALUE);
            user.setProfileData(defaultProfile);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace(); // Adjust error handling as needed
        }
    }
}