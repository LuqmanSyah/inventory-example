package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.StockDto;
import com.example.inventoryexample.entity.Stock;
import com.example.inventoryexample.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockRepository stockRepository;
    
    @Transactional(readOnly = true)
    public List<StockDto> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public StockDto getStockById(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok dengan ID " + id + " tidak ditemukan"));
        return convertToDto(stock);
    }
    
    @Transactional(readOnly = true)
    public StockDto getStockByProductId(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stok untuk produk ID " + productId + " tidak ditemukan"));
        return convertToDto(stock);
    }
    
    @Transactional(readOnly = true)
    public List<StockDto> getLowStocks() {
        return stockRepository.findLowStocks().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<StockDto> getOutOfStocks() {
        return stockRepository.findOutOfStocks().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public StockDto updateStock(Long id, StockDto stockDto) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok dengan ID " + id + " tidak ditemukan"));
        
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }
    
    @Transactional
    public StockDto addStock(Long productId, Integer amount) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stok untuk produk ID " + productId + " tidak ditemukan"));
        
        stock.addStock(amount);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }
    
    @Transactional
    public StockDto reduceStock(Long productId, Integer amount) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stok untuk produk ID " + productId + " tidak ditemukan"));
        
        stock.reduceStock(amount);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }
    
    private StockDto convertToDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setProductId(stock.getProduct().getId());
        dto.setProductName(stock.getProduct().getName());
        dto.setProductSku(stock.getProduct().getSku());
        dto.setCategoryName(stock.getProduct().getCategory() != null ? 
                stock.getProduct().getCategory().getName() : null);
        dto.setQuantity(stock.getQuantity());
        dto.setMinimumStock(stock.getMinimumStock());
        dto.setLastRestockDate(stock.getLastRestockDate());
        dto.setIsLowStock(stock.isLowStock());
        return dto;
    }
}
