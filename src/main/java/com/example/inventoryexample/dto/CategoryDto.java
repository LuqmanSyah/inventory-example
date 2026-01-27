package com.example.inventoryexample.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private Long id;
    
    @NotBlank(message = "Nama kategori tidak boleh kosong")
    private String name;
    
    private String description;
}
