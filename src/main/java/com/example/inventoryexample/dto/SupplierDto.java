package com.example.inventoryexample.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    
    private Long id;
    
    @NotBlank(message = "Nama supplier tidak boleh kosong")
    private String name;
    
    @NotBlank(message = "Alamat tidak boleh kosong")
    private String address;
    
    private String phoneNumber;
    
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;
    
    private String description;
}
