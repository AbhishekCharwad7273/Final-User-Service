package com.UserService.Services;

//import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.UserService.Entity.Role;
import com.UserService.Entity.User;
import com.UserService.Exception.UserException;
import com.UserService.JwtSecurity.JwtService;
import com.UserService.Repository.UserRepo;
import com.UserService.Security.AuthResponse;
import com.UserService.Security.Login;
import com.UserService.Security.Register;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static AuthResponse hideUserPassword(User savedUser, String token) {
        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .name(savedUser.getName())
                .emailId(savedUser.getEmailId())
                .password("***************")
                .authorUser(savedUser.isAuthorUser())
                .build();
    }

    public Optional<User> retrieveUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> retrieveUserByEmail(String email) {
        return userRepository.findByEmailId(email);
    }

    public AuthResponse addUser(Register registerRequest) {
        userRepository.findByEmailId(registerRequest.getEmailId()).ifPresent(x -> {
            throw new UserException("User Registration Error");
        });
        var user = User.builder()
                .name(registerRequest.getName())
                .emailId(registerRequest.getEmailId())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .subscriptions(new ArrayList<>())
                .authorUser(registerRequest.isAuthorUser())
                .role(registerRequest.isAuthorUser() ? Role.AUTHOR : Role.USER)
                .build();

        User newUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user.getEmailId());
        return hideUserPassword(newUser, jwtToken);
    }

    public AuthResponse loginUser(Login registerRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registerRequest.getEmailId(), registerRequest.getPassword()));
        var user = userRepository.findByEmailId(registerRequest.getEmailId()).orElseThrow(() -> new UserException("Login Error:"));
        var jwtToken = jwtService.generateToken(user.getEmailId());
        return hideUserPassword(user, jwtToken);
    }
}
