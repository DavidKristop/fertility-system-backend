package com.group3.backend.service;

import com.group3.backend.entity.Role;
import com.group3.backend.entity.UserInfo;
import com.group3.backend.entity.request.RegistrationRequest;
import com.group3.backend.models.Roles;
import com.group3.backend.repository.RoleRepository;
import com.group3.backend.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userDetail = repository.findByUsername(username);

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String registerUser(RegistrationRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username '" + request.getUsername() + "' already exists!");
        }
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists!");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setName(request.getUsername());
        userInfo.setEmail(request.getEmail());
        userInfo.setPassword(encoder.encode(request.getPassword()));
        Role patientRole = roleRepository.findByName(Roles.ROLE_PATIENT)
                .orElseThrow(() -> new RuntimeException("Role '" + Roles.ROLE_PATIENT.name() + "' not found in database."));
        userInfo.setRole(patientRole);

        repository.save(userInfo);
        return "User Registered Successfully.";
    }
}
