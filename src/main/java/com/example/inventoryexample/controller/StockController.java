package com.example.inventoryexample.controller;

import com.example.inventoryexample.dto.StockDto;
import com.example.inventoryexample.service.StockService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDto>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<StockDto> getStockByProductId(
        @PathVariable Long productId
    ) {
        return ResponseEntity.ok(stockService.getStockByProductId(productId));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<StockDto>> getLowStocks() {
        return ResponseEntity.ok(stockService.getLowStocks());
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<StockDto>> getOutOfStocks() {
        return ResponseEntity.ok(stockService.getOutOfStocks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDto> updateStock(
        @PathVariable Long id,
        @Valid @RequestBody StockDto stockDto
    ) {
        return ResponseEntity.ok(stockService.updateStock(id, stockDto));
    }

    @PostMapping("/product/{productId}/add")
    public ResponseEntity<StockDto> addStock(
        @PathVariable Long productId,
        @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(stockService.addStock(productId, quantity));
    }

    @PostMapping("/product/{productId}/reduce")
    public ResponseEntity<StockDto> reduceStock(
        @PathVariable Long productId,
        @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(stockService.reduceStock(productId, quantity));
    }
}
