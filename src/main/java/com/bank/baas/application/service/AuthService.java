package com.bank.baas.application.service;

import com.bank.baas.domain.exception.AlreadyExistsException;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.infrastructure.security.JwtTokenProvider;
import com.bank.baas.presentation.dto.AuthRequest;
import com.bank.baas.presentation.dto.AuthResponse;
import com.bank.baas.presentation.dto.UserRegistrationRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager,
                      JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User registerUser(UserRegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new AlreadyExistsException("Email already in use");
        }

        if (userRepository.existsByCpf(registrationRequest.getCpf())) {
            throw new AlreadyExistsException("CPF already in use");
        }

        User user = new User(
            registrationRequest.getEmail(),
            registrationRequest.getCpf(),
            passwordEncoder.encode(registrationRequest.getPassword())
        );

        return userRepository.save(user);
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(authentication);

        User user = userRepository.findUserByEmail(authRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(jwt, user.getId(), user.getEmail());
    }
}
