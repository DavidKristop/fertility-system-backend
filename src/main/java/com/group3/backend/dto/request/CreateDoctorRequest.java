package com.group3.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorRequest {

    // User info
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0|84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|89]|9[0-4|6-9])[0-9]{7}$", 
            message = "Phone number must be a valid Vietnam phone number")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Date of birth cannot be empty")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,32}$",
                message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters long")
    private String password;

    // DoctorProfile info
    @NotBlank(message = "Specialty is required")
    private String specialty;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotNull(message = "Years of experience is required")
    @Digits(integer = 3, fraction = 2, message = "Years of experience must be a valid number with up to 3 digits before the decimal and 2 digits after the decimal")
    private BigDecimal yearsOfExperience;

    @NotBlank(message = "License number is required")
    private String licenseNumber;
}
