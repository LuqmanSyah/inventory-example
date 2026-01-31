package com.example.inventoryexample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private Long id;
    
    @NotBlank(message = "Nama produk tidak boleh kosong")
    private String name;
    
    private String sku;
    
    private String description;
    
    @NotNull(message = "Harga tidak boleh kosong")
    @Positive(message = "Harga harus lebih dari 0")
    private BigDecimal price;
    
    @NotNull(message = "Category ID tidak boleh kosong")
    private Long categoryId;
    
    private String categoryName;
    
    @NotNull(message = "Supplier ID tidak boleh kosong")
    private Long supplierId;
    
    private String supplierName;
    
    // Stock info
    private Integer stockQuantity;
    private Integer minimumStock;
    private Boolean isLowStock;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
