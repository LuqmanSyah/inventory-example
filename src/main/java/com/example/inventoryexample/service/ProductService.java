package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.ProductDto;
import com.example.inventoryexample.entity.Category;
import com.example.inventoryexample.entity.Product;
import com.example.inventoryexample.entity.Stock;
import com.example.inventoryexample.entity.Supplier;
import com.example.inventoryexample.repository.CategoryRepository;
import com.example.inventoryexample.repository.ProductRepository;
import com.example.inventoryexample.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> {
                    if (p1.getUpdatedAt() == null && p2.getUpdatedAt() == null) return 0;
                    if (p1.getUpdatedAt() == null) return 1;
                    if (p2.getUpdatedAt() == null) return -1;
                    return p2.getUpdatedAt().compareTo(p1.getUpdatedAt());
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk dengan ID " + id + " tidak ditemukan"));
        return convertToDto(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierId(supplierId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori dengan ID " + productDto.getCategoryId() + " tidak ditemukan"));
        
        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier dengan ID " + productDto.getSupplierId() + " tidak ditemukan"));
        
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(category);
        product.setSupplier(supplier);
        
        // Buat stok default untuk produk baru
        Stock stock = new Stock();
        stock.setQuantity(productDto.getStockQuantity() != null ? productDto.getStockQuantity() : 0);
        stock.setMinimumStock(productDto.getMinimumStock() != null ? productDto.getMinimumStock() : 10);
        stock.setProduct(product);
        product.setStock(stock);
        
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk dengan ID " + id + " tidak ditemukan"));
        
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori dengan ID " + productDto.getCategoryId() + " tidak ditemukan"));
        
        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier dengan ID " + productDto.getSupplierId() + " tidak ditemukan"));
        
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(category);
        product.setSupplier(supplier);
        
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produk dengan ID " + id + " tidak ditemukan");
        }
        productRepository.deleteById(id);
    }
    
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setSupplierId(product.getSupplier().getId());
        dto.setSupplierName(product.getSupplier().getName());
        
        if (product.getStock() != null) {
            dto.setStockQuantity(product.getStock().getQuantity());
            dto.setMinimumStock(product.getStock().getMinimumStock());
            dto.setIsLowStock(product.getStock().isLowStock());
        }
        
        return dto;
    }
}
