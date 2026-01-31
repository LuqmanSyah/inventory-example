package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.LoginRequest;
import com.example.inventoryexample.dto.LoginResponse;
import com.example.inventoryexample.dto.ProfileUpdateRequest;
import com.example.inventoryexample.dto.UserDto;
import com.example.inventoryexample.entity.User;
import com.example.inventoryexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Username atau password salah"));
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Akun Anda tidak aktif. Hubungi administrator");
        }
        
        // BCrypt password verification
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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
                .sorted((u1, u2) -> {
                    if (u1.getUpdatedAt() == null && u2.getUpdatedAt() == null) return 0;
                    if (u1.getUpdatedAt() == null) return 1;
                    if (u2.getUpdatedAt() == null) return -1;
                    return u2.getUpdatedAt().compareTo(u1.getUpdatedAt());
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRoleString(String role) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            return userRepository.findByRole(userRole).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role " + role + " tidak valid. Gunakan SUPER_ADMIN, ADMIN, atau STAFF");
        }
    }
    
    @Transactional
    public UserDto createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username sudah digunakan");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    @Transactional
    public UserDto createUserFromDto(UserDto userDto) {
        return createUserFromDto(userDto, null);
    }
    
    @Transactional
    public UserDto createUserFromDto(UserDto userDto, Long requesterId) {
        // Validasi username unik
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username " + userDto.getUsername() + " sudah digunakan");
        }
        
        // Validasi email unik
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email " + userDto.getEmail() + " sudah digunakan");
        }
        
        // Validasi role
        User.UserRole targetRole = userDto.getRole() != null ? userDto.getRole() : User.UserRole.STAFF;
        validateRoleCreation(targetRole, requesterId);
        
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole(targetRole);
        user.setIsActive(userDto.getIsActive() != null ? userDto.getIsActive() : true);
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    /**
     * Validasi pembuatan role berdasarkan aturan:
     * - SUPER_ADMIN hanya boleh ada 1
     * - Hanya SUPER_ADMIN yang bisa membuat ADMIN
     * - ADMIN tidak bisa membuat ADMIN atau SUPER_ADMIN
     */
    private void validateRoleCreation(User.UserRole targetRole, Long requesterId) {
        // Validasi SUPER_ADMIN hanya boleh 1
        if (targetRole == User.UserRole.SUPER_ADMIN) {
            if (userRepository.existsByRole(User.UserRole.SUPER_ADMIN)) {
                throw new RuntimeException("Super Admin sudah ada. Hanya boleh ada 1 Super Admin dalam sistem.");
            }
        }
        
        // Jika requesterId ada, validasi permission
        if (requesterId != null) {
            User requester = userRepository.findById(requesterId).orElse(null);
            if (requester != null) {
                // ADMIN tidak bisa membuat ADMIN atau SUPER_ADMIN
                if (requester.getRole() == User.UserRole.ADMIN) {
                    if (targetRole == User.UserRole.ADMIN || targetRole == User.UserRole.SUPER_ADMIN) {
                        throw new RuntimeException("Admin tidak dapat membuat user dengan role Admin atau Super Admin. Hanya Super Admin yang dapat melakukan ini.");
                    }
                }
                // STAFF tidak bisa membuat user apapun
                if (requester.getRole() == User.UserRole.STAFF) {
                    throw new RuntimeException("Staff tidak memiliki akses untuk membuat user baru.");
                }
            }
        }
    }
    
    /**
     * Validasi perubahan role berdasarkan aturan
     */
    private void validateRoleChange(User.UserRole currentRole, User.UserRole newRole, Long requesterId) {
        // Jika role tidak berubah, tidak perlu validasi
        if (currentRole == newRole) {
            return;
        }
        
        // Tidak boleh mengubah menjadi SUPER_ADMIN jika sudah ada
        if (newRole == User.UserRole.SUPER_ADMIN && currentRole != User.UserRole.SUPER_ADMIN) {
            if (userRepository.existsByRole(User.UserRole.SUPER_ADMIN)) {
                throw new RuntimeException("Super Admin sudah ada. Hanya boleh ada 1 Super Admin dalam sistem.");
            }
        }
        
        if (requesterId != null) {
            User requester = userRepository.findById(requesterId).orElse(null);
            if (requester != null) {
                // ADMIN tidak bisa mengubah role ke ADMIN atau SUPER_ADMIN
                if (requester.getRole() == User.UserRole.ADMIN) {
                    if (newRole == User.UserRole.ADMIN || newRole == User.UserRole.SUPER_ADMIN) {
                        throw new RuntimeException("Admin tidak dapat mengubah role menjadi Admin atau Super Admin.");
                    }
                    // ADMIN tidak bisa mengubah role SUPER_ADMIN atau ADMIN lain
                    if (currentRole == User.UserRole.SUPER_ADMIN || currentRole == User.UserRole.ADMIN) {
                        throw new RuntimeException("Admin tidak dapat mengubah role Super Admin atau Admin lain.");
                    }
                }
            }
        }
    }
    
    @Transactional
    public UserDto updateUser(Long id, User userDetails) {
        return updateUser(id, userDetails, null);
    }
    
    @Transactional
    public UserDto updateUser(Long id, User userDetails, Long requesterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        // Validasi perubahan role
        if (userDetails.getRole() != null) {
            validateRoleChange(user.getRole(), userDetails.getRole(), requesterId);
        }
        
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setIsActive(userDetails.getIsActive());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Transactional
    public UserDto updateUserFromDto(Long id, UserDto userDto) {
        return updateUserFromDto(id, userDto, null);
    }
    
    @Transactional
    public UserDto updateUserFromDto(Long id, UserDto userDto, Long requesterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        // Validasi username unik (kecuali user saat ini)
        if (!user.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username " + userDto.getUsername() + " sudah digunakan");
        }
        
        // Validasi email unik (kecuali user saat ini)
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email " + userDto.getEmail() + " sudah digunakan");
        }
        
        // Validasi perubahan role
        if (userDto.getRole() != null && userDto.getRole() != user.getRole()) {
            validateRoleChange(user.getRole(), userDto.getRole(), requesterId);
        }
        
        user.setUsername(userDto.getUsername());
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
        
        if (userDto.getIsActive() != null) {
            user.setIsActive(userDto.getIsActive());
        }
        
        // Update password jika diisi
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        deleteUser(id, null);
    }
    
    @Transactional
    public void deleteUser(Long id, Long requesterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        // SUPER_ADMIN tidak dapat dihapus
        if (user.getRole() == User.UserRole.SUPER_ADMIN) {
            throw new RuntimeException("Super Admin tidak dapat dihapus.");
        }
        
        // Validasi requester permission
        if (requesterId != null) {
            User requester = userRepository.findById(requesterId).orElse(null);
            if (requester != null && requester.getRole() == User.UserRole.ADMIN) {
                // ADMIN tidak bisa menghapus ADMIN lain
                if (user.getRole() == User.UserRole.ADMIN) {
                    throw new RuntimeException("Admin tidak dapat menghapus Admin lain. Hanya Super Admin yang dapat melakukan ini.");
                }
            }
        }
        
        userRepository.deleteById(id);
    }
    
    @Transactional
    public UserDto toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Transactional
    public String resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        // Generate random password
        String newPassword = generateRandomPassword(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return newPassword;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long superAdminCount = allUsers.stream().filter(u -> u.getRole() == User.UserRole.SUPER_ADMIN).count();
        long adminCount = allUsers.stream().filter(u -> u.getRole() == User.UserRole.ADMIN).count();
        long staffCount = allUsers.stream().filter(u -> u.getRole() == User.UserRole.STAFF).count();
        long activeUsers = allUsers.stream().filter(User::getIsActive).count();
        long inactiveUsers = totalUsers - activeUsers;
        
        stats.put("totalUsers", totalUsers);
        stats.put("superAdminCount", superAdminCount);
        stats.put("adminCount", adminCount);
        stats.put("staffCount", staffCount);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        
        return stats;
    }
    
    private String generateRandomPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return password.toString();
    }
    
    @Transactional
    public UserDto updateUserProfile(Long id, ProfileUpdateRequest profileRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan"));
        
        user.setFullName(profileRequest.getFullName());
        user.setEmail(profileRequest.getEmail());
        user.setPhoneNumber(profileRequest.getPhoneNumber());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}
