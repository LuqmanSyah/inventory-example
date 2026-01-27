package com.example.inventoryexample.repository;

import com.example.inventoryexample.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findBySupplierId(Long supplierId);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.supplier.id = :supplierId")
    List<Product> findByCategoryIdAndSupplierId(Long categoryId, Long supplierId);
    
    boolean existsBySku(String sku);
}
