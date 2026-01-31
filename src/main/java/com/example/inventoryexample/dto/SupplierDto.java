package com.example.inventoryexample.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    
    @Pattern(regexp = "^(\\+62|62|0)[0-9]{8,13}$", message = "Format nomor telepon tidak valid. Gunakan format: 08xx, 62xx, atau +62xx (9-14 digit)")
    private String phoneNumber;
    
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Format email tidak valid")
    private String email;
    
    private String description;
}
