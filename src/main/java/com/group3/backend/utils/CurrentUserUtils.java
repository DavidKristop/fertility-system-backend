package com.group3.backend.utils;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.User;
import com.group3.backend.service.UserDetailsImpl;

@Component
public class CurrentUserUtils {

    private static final String UNAUTHORIZED_MESSAGE = "User not authenticated";

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE);
        }
        
        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof UserDetailsImpl)) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE);
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        return userDetails.getUser();
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public String getCurrentUserRole() {
        return getCurrentUser().getRole().getName().name();
    }
}
