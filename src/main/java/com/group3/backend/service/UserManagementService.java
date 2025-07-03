package com.group3.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.model.User;
import com.group3.backend.repository.UserRepository;

@Service
public class UserManagementService {
    @Autowired
    private UserRepository userRepository;

    public Page<User> getUsers(String roleName, String fullName, boolean isActive, Pageable pageable) {
        return userRepository.findAllByRoleNameAndFullNameIgnoreCaseContainingAndIsActive(roleName, fullName, isActive, pageable);
    }
}
