package com.example.inventoryexample.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama supplier tidak boleh kosong")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Alamat tidak boleh kosong")
    @Column(nullable = false)
    private String address;
    
    @Pattern(regexp = "^(\\+62|62|0)[0-9]{8,13}$", message = "Format nomor telepon tidak valid. Gunakan format: 08xx, 62xx, atau +62xx (9-14 digit)")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Format email tidak valid")
    @Column(nullable = false)
    private String email;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relasi One-to-Many dengan Product
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
