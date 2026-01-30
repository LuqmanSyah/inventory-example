package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.LoginRequest;
import com.example.inventoryexample.dto.LoginResponse;
import com.example.inventoryexample.dto.UserDto;
import com.example.inventoryexample.entity.User;
import com.example.inventoryexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Username atau password salah"));
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Akun Anda tidak aktif. Hubungi administrator");
        }
        
        // Simple password check (dalam production, gunakan BCrypt)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Username atau password salah");
        }
        
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMessage("Login berhasil");
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        return convertToDto(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserDto createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username sudah digunakan");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    @Transactional
    public UserDto updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setIsActive(userDetails.getIsActive());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User dengan ID " + id + " tidak ditemukan");
        }
        userRepository.deleteById(id);
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}
