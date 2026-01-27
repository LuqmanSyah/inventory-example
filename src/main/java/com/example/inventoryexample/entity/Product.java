package com.example.inventoryexample.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama produk tidak boleh kosong")
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String sku; // Stock Keeping Unit
    
    @Column(length = 1000)
    private String description;
    
    @NotNull(message = "Harga tidak boleh kosong")
    @Positive(message = "Harga harus lebih dari 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relasi Many-to-One dengan Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;
    
    // Relasi Many-to-One dengan Supplier
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnore
    private Supplier supplier;
    
    // Relasi One-to-One dengan Stock
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sku == null || sku.isEmpty()) {
            sku = generateSku();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateSku() {
        return "PRD-" + System.currentTimeMillis();
    }
}
