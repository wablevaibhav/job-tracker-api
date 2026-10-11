package com.vaibhavwable.jobtracker.service;

import com.vaibhavwable.jobtracker.dto.AuthDtos.AuthResponse;
import com.vaibhavwable.jobtracker.dto.AuthDtos.LoginRequest;
import com.vaibhavwable.jobtracker.dto.AuthDtos.RegisterRequest;
import com.vaibhavwable.jobtracker.exception.DuplicateEmailException;
import com.vaibhavwable.jobtracker.model.AppUser;
import com.vaibhavwable.jobtracker.repository.UserRepository;
import com.vaibhavwable.jobtracker.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        userRepository.save(new AppUser(email, passwordEncoder.encode(request.password())));
        return new AuthResponse(jwtService.generateToken(email));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        return new AuthResponse(jwtService.generateToken(email));
    }
}
