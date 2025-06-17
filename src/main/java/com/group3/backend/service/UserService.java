package com.group3.backend.service;

import com.group3.backend.model.Role;
import com.group3.backend.model.User;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.constants.Roles;
import com.group3.backend.repository.RoleRepository;
import com.group3.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new UserDetailsImpl(user);
    }

    public String registerUser(RegistrationRequest request) {
        if (userRepository.findByFullName(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with full name '" + request.getUsername() + "' already exists!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists!");
        }

        Role patientRole = roleRepository.findByName(Roles.ROLE_PATIENT)
                .orElseThrow(() -> new RuntimeException("Role '" + Roles.ROLE_PATIENT.name() + "' not found."));

        User newUser = User.builder()
                .fullName(request.getUsername())
                .email(request.getEmail())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret("DEFAULT") // hoặc random chuỗi bảo mật nếu bạn cần
                .dateOfBirth(LocalDate.of(2000, 1, 1)) // placeholder
                .role(patientRole)
                .build();

        userRepository.save(newUser);
        return "User Registered Successfully.";
    }
}
