package com.group3.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.model.User;
import com.group3.backend.model.VerifyEmailToken;
import com.group3.backend.repository.VerifyEmailTokenRepository;
import com.group3.backend.utils.Constants;
@Service
public class VerifyTokenService {
    @Autowired
    private VerifyEmailTokenRepository verifyEmailTokenRepository;

    public VerifyEmailToken createVerifyToken(User user, String token) {
        if(user.isVerify()){
            throw new ResourceConflictException("User is already verified");
        }

        verifyEmailTokenRepository.deleteByUser(user);
        verifyEmailTokenRepository.flush();
        
        VerifyEmailToken verifyToken = new VerifyEmailToken();
        verifyToken.setToken(token);
        verifyToken.setUser(user);
        verifyToken.setExpiryDate(LocalDateTime.now().plusMinutes(Constants.MAX_VERIFY_EMAIL_TOKEN_MINUTES));
        
        return verifyEmailTokenRepository.save(verifyToken);
    }
}
