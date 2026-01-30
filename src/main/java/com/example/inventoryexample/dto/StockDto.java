package com.example.inventoryexample.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {
    
    private Long id;
    
    @NotNull(message = "Product ID tidak boleh kosong")
    private Long productId;
    
    private String productName;
    
    private String productSku;
    
    private String categoryName;
    
    @NotNull(message = "Jumlah stok tidak boleh kosong")
    @Min(value = 0, message = "Jumlah stok tidak boleh kurang dari 0")
    private Integer quantity;
    
    @Min(value = 0, message = "Minimum stok tidak boleh kurang dari 0")
    private Integer minimumStock;
    
    private LocalDateTime lastRestockDate;
    
    private Boolean isLowStock;
}
