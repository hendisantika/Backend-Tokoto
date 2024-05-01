package com.apiecommerce.tokoto.auth;

import com.apiecommerce.tokoto.user.User;
import com.apiecommerce.tokoto.user.UserProjection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/saveUserProfile")
    public ResponseEntity<Void> saveUserProfile(
            @PathVariable String email,
            @RequestPart("file") MultipartFile file
    ) {
        service.saveUserProfile(email, file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/findUserByEmail/{email}")
    public ResponseEntity<UserProjection> findUserByEmail(
            @RequestParam String email
    ) {
        UserProjection projection = service.findUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(projection);
    }

    @GetMapping("/findUserWithRegency/{provinceName}/{regencyName}")
    public ResponseEntity<List<UserProjection>> findUserWithRegency(
            @PathVariable String provinceName,
            @PathVariable String regencyName
    ) {
        List<UserProjection> projectionList = service.findUserWithRegency(provinceName, regencyName);
        return ResponseEntity.status(HttpStatus.OK).body(projectionList);
    }
}