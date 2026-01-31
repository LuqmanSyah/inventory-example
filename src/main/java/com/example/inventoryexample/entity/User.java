package com.example.inventoryexample.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username tidak boleh kosong")
    @Column(nullable = false, unique = true)
    private String username;
    
    @NotBlank(message = "Password tidak boleh kosong")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Column(nullable = false)
    private String fullName;
    
    @Email(message = "Format email tidak valid")
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum UserRole {
        SUPER_ADMIN("Super Admin", "Akses penuh ke semua fitur termasuk manajemen admin"),
        ADMIN("Admin", "Akses penuh ke fitur kecuali membuat admin baru"),
        STAFF("Staff", "Akses terbatas ke kelola produk, stok, dan supplier");
        
        private final String displayName;
        private final String description;
        
        UserRole(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
