package com.example.inventoryexample.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    
    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    private String fullName;
    
    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email tidak boleh kosong")
    private String email;
    
    private String phoneNumber;
}
