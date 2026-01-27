package com.example.inventoryexample.repository;

import com.example.inventoryexample.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    Optional<Stock> findByProductId(Long productId);
    
    @Query("SELECT s FROM Stock s WHERE s.quantity <= s.minimumStock")
    List<Stock> findLowStocks();
    
    @Query("SELECT s FROM Stock s WHERE s.quantity = 0")
    List<Stock> findOutOfStocks();
}
