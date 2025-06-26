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

    //Register a new patient
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
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString()) // tạo mật khẩu bí mật ngẫu nhiên
                .dateOfBirth(request.getDateOfBirth())
                .role(patientRole)
                .build();

        userRepository.save(newUser);
        return "User Registered Successfully.";
    }

    // Create a new doctor account
    public String createDoctorAccount(RegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists!");
        }

        Role doctorRole = roleRepository.findByName(Roles.ROLE_DOCTOR)
                .orElseThrow(() -> new RuntimeException("Doctor role not found."));

        User user = User.builder()
                .fullName(request.getUsername())
                .email(request.getEmail())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString())
                .dateOfBirth(request.getDateOfBirth())
                .role(doctorRole)
                .build();

        userRepository.save(user);
        return "Doctor account created successfully.";
    }

    // Create a new manager account
    public String createManagerAccount(RegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists!");
        }

        Role managerRole = roleRepository.findByName(Roles.ROLE_MANAGER)
                .orElseThrow(() -> new RuntimeException("Manager role not found."));

        User user = User.builder()
                .fullName(request.getUsername())
                .email(request.getEmail())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString())
                .dateOfBirth(request.getDateOfBirth())
                .role(managerRole)
                .build();

        userRepository.save(user);
        return "Manager account created successfully.";
    }

        public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
