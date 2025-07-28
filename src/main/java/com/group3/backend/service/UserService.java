package com.group3.backend.service;

import com.group3.backend.model.DoctorProfile;
import com.group3.backend.model.PatientProfile;
import com.group3.backend.model.Role;
import com.group3.backend.model.User;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.request.CreateDoctorRequest;
import com.group3.backend.dto.request.CreatePatientRequest;
import com.group3.backend.dto.response.DoctorResponse;
import com.group3.backend.dto.response.ManagedUserResponse;
import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.mapper.ManagedUserMapper;
import com.group3.backend.constants.Roles;
import com.group3.backend.repository.RoleRepository;
import com.group3.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManagedUserMapper managedUserMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new UserDetailsImpl(user);
    }

    private User createUser(RegistrationRequest request, Roles newUserRole){
        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        
        if (age < 18) {
            throw new IllegalArgumentException("You must be at least 18 years old to register.");
        }

        Role role = roleRepository.findByName(newUserRole)
                .orElseThrow(() -> new RuntimeException("Role '" + newUserRole.name() + "' not found."));

        User newUser = User.builder()
                .fullName(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHashed(passwordEncoder.encode(request.getPassword()))
                .passwordSecret(UUID.randomUUID().toString())
                .dateOfBirth(request.getDateOfBirth())
                .role(role)
                .build();

        return newUser;
    }

    public User registerUser(RegistrationRequest request, Roles newUserRole) {

        return userRepository.save(createUser(request, newUserRole));
    }

    public User createDoctorUser(CreateDoctorRequest request){
        User newUser = createUser(request, Roles.ROLE_DOCTOR);
        DoctorProfile profile = DoctorProfile.builder()
            .specialty(request.getSpecialty())
            .degree(request.getDegree())
            .yearsOfExperience(request.getYearsOfExperience())
            .licenseNumber(request.getLicenseNumber())
            .user(newUser)
            .build();

        newUser.setDoctorProfile(profile);
        
        
        return newUser;
    }

    // Method to create a Doctor account
    public User registerDoctorAccount(CreateDoctorRequest request) {
        return userRepository.save(createDoctorUser(request));
    }

    public User createPatientUser(CreatePatientRequest request){
        User newUser = createUser(request, Roles.ROLE_PATIENT);
        PatientProfile profile = PatientProfile.builder()
            .medicalHistory(request.getMedicalHistory())
            .user(newUser)
            .build();

        newUser.setPatientProfile(profile);
        return newUser;
    }

    public User registerPatientAccount(CreatePatientRequest request) {
        return userRepository.save(createPatientUser(request));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public Page<ManagedUserResponse> getUsers(String role, String email, Pageable pageable) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<User> users;

        if (role == null || role.isBlank()) {
            users = userRepository.findByEmailExcludingCurrent(email, currentEmail, pageable);
        } else {
            Roles enumRole;
            try {
                enumRole = Roles.valueOf(role.toUpperCase()); // phải là: ROLE_DOCTOR, ROLE_ADMIN, ...
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }

            users = userRepository.findByRoleNameAndEmailExcludingCurrent(enumRole, email, currentEmail, pageable);
        }

        return users.map(managedUserMapper::toManagedUserResponse);
    }

    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setActive(false);
        userRepository.save(user);
    }

    public void reactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setActive(true);
        userRepository.save(user);
    }

    public ManagedUserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return managedUserMapper.toManagedUserResponse(user);
    }
}
