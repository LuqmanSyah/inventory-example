package com.example.inventoryexample.controller;

import com.example.inventoryexample.dto.LoginRequest;
import com.example.inventoryexample.dto.LoginResponse;
import com.example.inventoryexample.dto.ProfileUpdateRequest;
import com.example.inventoryexample.dto.UserDto;
import com.example.inventoryexample.entity.User;
import com.example.inventoryexample.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }
    
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable User.UserRole role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }
    
    @PutMapping("/user/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }
    
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateCurrentUserProfile(
            @RequestParam Long userId,
            @Valid @RequestBody ProfileUpdateRequest profileRequest) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, profileRequest));
    }
}
