package com.example.inventoryexample.controller;

import com.example.inventoryexample.dto.ProductDto;
import com.example.inventoryexample.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }
    
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductDto>> getProductsBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(productService.getProductsBySupplier(supplierId));
    }
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
