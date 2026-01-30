package com.example.inventoryexample.dto;

import com.example.inventoryexample.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private String fullName;
    
    @Email(message = "Format email tidak valid")
    private String email;
    
    private String phoneNumber;
    
    private User.UserRole role;
    
    private Boolean isActive;
}
