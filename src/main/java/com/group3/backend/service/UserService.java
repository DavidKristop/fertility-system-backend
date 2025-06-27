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
import java.time.Period;
import java.util.UUID;

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

    public User registerUser(RegistrationRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists!");
        }

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        
        if (age < 18) {
            throw new IllegalArgumentException("You must be at least 18 years old to register.");
        }

        Role patientRole = roleRepository.findByName(Roles.ROLE_PATIENT)
                .orElseThrow(() -> new RuntimeException("Role '" + Roles.ROLE_PATIENT.name() + "' not found."));

        User newUser = User.builder()
                .fullName(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString())
                .dateOfBirth(request.getDateOfBirth())
                .role(patientRole)
                .build();

        return userRepository.save(newUser);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
