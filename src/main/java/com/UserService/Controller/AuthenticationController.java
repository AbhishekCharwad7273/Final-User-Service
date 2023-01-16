package com.UserService.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.UserService.Security.AuthResponse;
import com.UserService.Security.Login;
import com.UserService.Security.Register;

@RestController
@RequestMapping("/api/v1/digitalbooks/authentication/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {
    private final com.UserService.Services.UserService userService;

    @PostMapping("sign-in")
    public ResponseEntity<AuthResponse> login(@RequestBody Login loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @PostMapping("sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody Register registerRequest) {
        return ResponseEntity.ok(userService.addUser(registerRequest));
    }
}
