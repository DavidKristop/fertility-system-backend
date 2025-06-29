package com.group3.backend.service;

import com.group3.backend.model.DoctorProfile;
import com.group3.backend.model.Role;
import com.group3.backend.model.User;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.request.CreateDoctorRequest;
import com.group3.backend.dto.response.DoctorResponse;
import com.group3.backend.constants.Roles;
import com.group3.backend.repository.DoctorProfileRepository;
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

import javax.print.Doc;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorProfileRepository doctorProfileRepository;

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

    // Method to create a Manager account
    public User createManagerAccount(RegistrationRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists!");
        }

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        
        if (age < 18) {
            throw new IllegalArgumentException("This user must be at least 18 years old.");
        }

        Role managerRole = roleRepository.findByName(Roles.ROLE_MANAGER)
                .orElseThrow(() -> new RuntimeException("Role '" + Roles.ROLE_MANAGER.name() + "' not found."));

        User newUser = User.builder()
                .fullName(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString())
                .dateOfBirth(request.getDateOfBirth())
                .role(managerRole)
                .build();

        return userRepository.save(newUser);
    }

    // Method to create a Doctor account
    public DoctorResponse createDoctorAccount(CreateDoctorRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists!");
        }

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();

        if (age < 18) {
            throw new IllegalArgumentException("This user must be at least 18 years old.");
        }

        Role doctorRole = roleRepository.findByName(Roles.ROLE_DOCTOR)
                .orElseThrow(() -> new RuntimeException("Role '" + Roles.ROLE_DOCTOR.name() + "' not found."));

        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString())
                .dateOfBirth(request.getDateOfBirth())
                .role(doctorRole)
                .build();

        userRepository.save(newUser);
        
        DoctorProfile profile = DoctorProfile.builder()
            .specialty(request.getSpecialty())
            .degree(request.getDegree())
            .yearsOfExperience(request.getYearsOfExperience())
            .licenseNumber(request.getLicenseNumber())
            .user(newUser)
            .build();

        newUser.setDoctorProfile(profile);
        
        doctorProfileRepository.save(profile);

        return new DoctorResponse(
                newUser.getId(),
                newUser.getFullName(),
                newUser.getEmail(),
                newUser.getPhone(),
                newUser.getAddress(),
                request.getSpecialty(),
                request.getDegree(),
                request.getYearsOfExperience(),
                request.getLicenseNumber()
        );
    }

        public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
