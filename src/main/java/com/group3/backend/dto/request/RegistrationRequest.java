package com.group3.backend.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Data
public class RegistrationRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^(0|84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|89]|9[0-4|6-9])[0-9]{7}$", 
            message = "Phone number must be a valid Vietnam phone number")
    private String phone;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotNull(message = "Date of birth cannot be empty")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,32}$",
                message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters long")
    private String password;

}
