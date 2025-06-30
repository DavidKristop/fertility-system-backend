package com.group3.backend.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.group3.backend.mapper.AppointmentRequestMapper;
import com.group3.backend.mapper.ContractMapper;
import com.group3.backend.mapper.DrugMapper;
import com.group3.backend.mapper.PaymentMapper;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.mapper.UserMapper;

@Configuration
public class MapperConfig {

    @Bean
    @Primary
    public AppointmentRequestMapper appointmentRequestMapper() {
        return Mappers.getMapper(AppointmentRequestMapper.class);
    }

    @Bean
    @Primary
    public ContractMapper contractMapper() {
        return Mappers.getMapper(ContractMapper.class);
    }

    @Bean
    @Primary
    public DrugMapper drugMapper() {
        return Mappers.getMapper(DrugMapper.class);
    }

    @Bean
    @Primary
    public TreatmentMapper treatmentMapper() {
        return Mappers.getMapper(TreatmentMapper.class);
    }

    @Bean
    @Primary
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    @Primary
    public PaymentMapper paymentMapper() {
        return Mappers.getMapper(PaymentMapper.class);
    }
}
