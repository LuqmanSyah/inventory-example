package com.example.inventoryexample.dto;

import com.example.inventoryexample.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private Long id;
    
    private String username;
    
    private String fullName;
    
    private String email;
    
    private User.UserRole role;
    
    private String message;
}
