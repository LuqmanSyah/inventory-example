package com.example.inventoryexample.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Jumlah stok tidak boleh kosong")
    @Min(value = 0, message = "Jumlah stok tidak boleh kurang dari 0")
    @Column(nullable = false)
    private Integer quantity = 0;
    
    @Min(value = 0, message = "Minimum stok tidak boleh kurang dari 0")
    @Column(name = "minimum_stock")
    private Integer minimumStock = 10;
    
    @Column(name = "last_restock_date")
    private LocalDateTime lastRestockDate;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relasi One-to-One dengan Product
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @JsonIgnore
    private Product product;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Method untuk cek apakah stok rendah
    public boolean isLowStock() {
        return quantity <= minimumStock;
    }
    
    // Method untuk menambah stok
    public void addStock(Integer amount) {
        this.quantity += amount;
        this.lastRestockDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Method untuk mengurangi stok
    public void reduceStock(Integer amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("Stok tidak mencukupi");
        }
    }
}
