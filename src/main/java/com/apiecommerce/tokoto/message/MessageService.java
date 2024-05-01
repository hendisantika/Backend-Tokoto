package com.apiecommerce.tokoto.message;

import com.apiecommerce.tokoto.config.JwtService;
import com.apiecommerce.tokoto.config.RSAEncryptConverter;
import com.apiecommerce.tokoto.exception.UnauthorizationException;
import com.apiecommerce.tokoto.token.TokenRepository;
import com.apiecommerce.tokoto.user.User;
import com.apiecommerce.tokoto.user.UserRepository;
import com.apiecommerce.tokoto.utils.ImageUtils;
import jakarta.mail.Multipart;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${file.message.path}")
    private String uploadPath;

    @Value("${host}")
    private String HOST;

    private final String IMAGE_URL = "http://" + HOST + ":8000/api/images/";

    public MessageResponse sendMessage(String emailRecepient, String content) {
        try {
            // Get Token User, Expired Or No?
            String token = extractTokenFromBearerHeader();
            if (token == null) {
                throw new UnauthorizationException("Token telah expired / tidak ditemukan! Harap login sekali lagi terhadap akun anda");
            }

            String userEmail = getUserDetailsFromToken(token);
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            User sender = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

            User penerima = userRepository.findByEmail(emailRecepient)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat Email : " + emailRecepient + " Tidak Dapat Ditemukan!"));
            if (penerima != null) {
                Message pesan = Message.builder()
                        .sender(sender)
                        .recipient(penerima)
                        .content(content)
                        .createdAt(LocalDateTime.now())
                        .build();
                messageRepository.save(pesan);

                return toMessageResponse(pesan);
            } else {
                throw new RuntimeException("Penerima tidak ditemukan!");
            }
        } catch (ServletException e) {
	        throw new RuntimeException(e);
        } catch (IOException e) {
	        throw new RuntimeException(e);
        }
    }

    public MessageResponse sendMessageWithAttachment(String emailRecepient, String content, MultipartFile file) {
       try {
           // Get Token User, Expired Or No?
           String token = extractTokenFromBearerHeader();
           if (token == null) {
               throw new UnauthorizationException("Token telah expired / tidak ditemukan! Harap login sekali lagi terhadap akun anda");
           }

           String userEmail = getUserDetailsFromToken(token);
           Optional<User> userOptional = userRepository.findByEmail(userEmail);
           User sender = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

           byte[] imageDatas = file.getBytes();

           User penerima = userRepository.findByEmail(emailRecepient)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat Email : " + emailRecepient + " Tidak Dapat Ditemukan!"));

           String originalFileName = file.getOriginalFilename();
           String hashedFileName = ImageUtils.hashFileName(originalFileName, file.getBytes());
           byte[] imageData = file.getBytes();
           Path imagePath = Paths.get(uploadPath, hashedFileName);
           Files.write(imagePath, imageData);

           if (penerima != null) {
               Message message = Message.builder()
                       .sender(sender)
                       .recipient(penerima)
                       .content(content)
                       .attachmentName(hashedFileName)
                       .attachmentData(imageDatas)
                       .attachmentType(file.getContentType())
                       .pathAttachment(IMAGE_URL + hashedFileName)
                       .createdAt(LocalDateTime.now())
                       .build();
               messageRepository.save(message);

               return toMessageResponse(message);
           } else {
               throw new RuntimeException("User tidak dapat ditemukan");
           }
       } catch (IOException e) {
           e.printStackTrace();
           return null;
       } catch (ServletException e) {
	       throw new RuntimeException(e);
       }
    }

    private MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .sender(message.getSender().getEmail())
                .recepient(message.getSender().getEmail())
                .content(message.getContent())
                .attachmentName(message.getAttachmentName())
                .pathAttachment(message.getPathAttachment())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private String getUserDetailsFromToken(String token) {
        String userEmail = jwtService.extractUsername(token);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(token, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        return userEmail;
    }

    private String extractTokenFromBearerHeader() throws ServletException, IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = null;
        FilterChain filterChain = null;
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return null;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        return jwt;
    }
}
