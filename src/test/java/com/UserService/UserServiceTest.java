package com.UserService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.UserService.Entity.User;
import com.UserService.Exception.UserException;
import com.UserService.JwtSecurity.JwtService;
import com.UserService.Repository.UserRepo;
import com.UserService.Security.AuthResponse;
import com.UserService.Security.Login;
import com.UserService.Security.Register;
import com.UserService.Services.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class UserServiceTest {
    @Mock
    UserRepo userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    UserService userService;

    @BeforeEach
    public final void setup() {
        userService = new UserService(userRepository, new BCryptPasswordEncoder(), new JwtService(), authenticationManager);
    }

    @Test
    void addUser_ValidTest() {
        JwtService jwtService = new JwtService();
      
        User expectedUser = User.builder()
                .id(1L)
                .password("psasdss")
                .emailId("abc@gmail.com")
                .name("abc124")
                .authorUser(true)
                .build();

        Register registerRequest = Register.builder()
                .emailId("abc@gmail.com")
                .password("pasdss")
                .name("abc124")
                .authorUser(true)
                .build();

        
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

       
        AuthResponse response = userService.addUser(registerRequest);

        
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo(registerRequest.getName());
        assertThat(response.getEmailId()).isEqualTo(registerRequest.getEmailId());
        assertThat(response.getPassword()).isEqualTo("***************");
        assertThat(response.getToken()).isEqualTo(jwtService.generateToken(registerRequest.getEmailId()));
    }

    @Test
    void addUser_InValidTest() {
        
        String errorMsg = "Email Already Registered";
        User expectedUser = User.builder()
        		   .id(1L)
                   .password("psasdss")
                   .emailId("abc@gmail.com")
                   .name("abc124")
                   .authorUser(true)
                   .build();

        Register registerRequest = Register.builder()
        		  .emailId("abc@gmail.com")
                  .password("pasdss")
                  .name("abc124")
                  .authorUser(true)
                  .build();

        
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(expectedUser));

        
        assertThatThrownBy(() -> userService.addUser(registerRequest), errorMsg)
                .isInstanceOf(UserException.class);
    }

    @Test
    void loginUser_ValidTest() {
        JwtService jwtService = new JwtService();
       
        User expectedUser = User.builder()
        		 .id(1L)
                 .password("psasdss")
                 .emailId("abc@gmail.com")
                 .name("abc124")
                 .authorUser(true)
                 .build();

        Login registerRequest = Login.builder()
        		 .emailId("abc@gmail.com")
                 .password("pasdss")
                .build();

     
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(expectedUser));
        Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

       
        AuthResponse response = userService.loginUser(registerRequest);

       
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmailId()).isEqualTo(registerRequest.getEmailId());
        assertThat(response.getPassword()).isEqualTo("***************");
        assertThat(response.getToken()).isEqualTo(jwtService.generateToken(registerRequest.getEmailId()));
    }

    @Test
    void loginUser_InValidTest() {
        String errorMsg = "User or Password Invalid";
       
        User expectedUser = User.builder()
        		   .id(1L)
                   .password("psasdss")
                   .emailId("abc@gmail.com")
                   .name("abc124")
                   .authorUser(true)
                   .build();

        Login registerRequest = Login.builder()
        		 .emailId("abc@gmail.com")
                 .password("pasdss")
                .build();
       
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

        
        assertThatThrownBy(() -> userService.loginUser(registerRequest), errorMsg).isInstanceOf(UserException.class);
    }
}