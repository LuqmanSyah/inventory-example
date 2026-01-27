package com.example.inventoryexample.controller;

import com.example.inventoryexample.dto.SupplierDto;
import com.example.inventoryexample.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SupplierDto>> searchSuppliers(@RequestParam String name) {
        return ResponseEntity.ok(supplierService.searchSuppliersByName(name));
    }
    
    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createSupplier(supplierDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDto> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDto supplierDto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
