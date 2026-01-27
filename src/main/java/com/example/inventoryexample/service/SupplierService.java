package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.SupplierDto;
import com.example.inventoryexample.entity.Supplier;
import com.example.inventoryexample.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    
    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier dengan ID " + id + " tidak ditemukan"));
        return convertToDto(supplier);
    }
    
    @Transactional(readOnly = true)
    public List<SupplierDto> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierDto.getName());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setPhoneNumber(supplierDto.getPhoneNumber());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setDescription(supplierDto.getDescription());
        
        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToDto(savedSupplier);
    }
    
    @Transactional
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier dengan ID " + id + " tidak ditemukan"));
        
        supplier.setName(supplierDto.getName());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setPhoneNumber(supplierDto.getPhoneNumber());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setDescription(supplierDto.getDescription());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToDto(updatedSupplier);
    }
    
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier dengan ID " + id + " tidak ditemukan");
        }
        supplierRepository.deleteById(id);
    }
    
    private SupplierDto convertToDto(Supplier supplier) {
        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setAddress(supplier.getAddress());
        dto.setPhoneNumber(supplier.getPhoneNumber());
        dto.setEmail(supplier.getEmail());
        dto.setDescription(supplier.getDescription());
        return dto;
    }
}
