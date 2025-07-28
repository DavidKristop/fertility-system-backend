package com.group3.backend.dto.request;


import java.time.LocalDate;

import com.group3.backend.constants.RegexPattern;
import com.group3.backend.constants.Roles;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegistrationRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = RegexPattern.PHONE_NUMBER, message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotNull(message = "Date of birth cannot be empty")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = RegexPattern.PASSWORD,
                message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters long")
    private String password;
}
