package com.example.inventoryexample.dto;

import com.example.inventoryexample.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;
    
    private String fullName;
    
    @Email(message = "Format email tidak valid")
    private String email;
    
    private User.UserRole role;
    
    private Boolean isActive;
}
