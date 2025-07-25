package com.group3.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.backend.model.Refund;
import com.group3.backend.repository.RefundRepository;

@Service
public class RefundService {
    @Autowired
    private RefundRepository refundRepository;
    
    public Refund createRefund(Refund refund){
        return refundRepository.save(refund);
    }
}
