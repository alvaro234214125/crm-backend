package com.dev.backend_crm.controller;

import com.dev.backend_crm.dto.AuthRequest;
import com.dev.backend_crm.dto.AuthResponse;
import com.dev.backend_crm.dto.RegisterRequest;
import com.dev.backend_crm.dto.UserDto;
import com.dev.backend_crm.security.JwtService;
import com.dev.backend_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request){
        if(userService.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exist");
        }
        UserDto userDto = UserDto.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .status(request.getStatus())
                .build();
        userService.register(userDto);
        String token = jwtService.generarToken(userDto.getEmail());
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        String token = jwtService.generarToken(authRequest.getEmail());
        return new AuthResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName(); // viene del token (sub)
        UserDto user = userService.getByEmail(email);
        return ResponseEntity.ok(user);
    }

}
