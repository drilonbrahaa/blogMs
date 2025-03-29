package com.example.blogMs.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogMs.entities.Role;
import com.example.blogMs.entities.User;
import com.example.blogMs.payload.JwtResponse;
import com.example.blogMs.payload.LoginRequest;
import com.example.blogMs.payload.SignupRequest;
import com.example.blogMs.repositories.UserRepository;
import com.example.blogMs.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtil jwtUtil;
    
    @PostMapping("/signin")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
          
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(loginRequest.getUsername());
        
        List<String> roles = authentication.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        
        return new JwtResponse(jwt, loginRequest.getUsername(), roles);
    }
    
    @PostMapping("/signup")
    public String registerUser(@RequestBody SignupRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return "Error: Username is already taken!";
        }
        
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        if(signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            roles.add(Role.READER);
        } else {
            signUpRequest.getRoles().forEach(role -> {
                switch(role) {
                    case "admin" -> roles.add(Role.ADMIN);
                    case "author" -> roles.add(Role.AUTHOR);
                    default -> roles.add(Role.READER);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "User registered successfully!";
    }
}
