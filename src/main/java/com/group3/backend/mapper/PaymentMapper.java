package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.PaymentResponse;
import com.group3.backend.dto.response.RefundResponse;
import com.group3.backend.model.Payment;
import com.group3.backend.model.Refund;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TreatmentMapper.class})
public interface PaymentMapper {
    @Mapping(source = "user.id", target = "userId")
    PaymentResponse toResponse(Payment payment);

    @Mapping(source = "payment.id", target = "paymentId")
    @Mapping(source = "user.id", target = "userId")
    RefundResponse toRefundResponse(Refund refund);
}
