package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.StockDto;
import com.example.inventoryexample.entity.Category;
import com.example.inventoryexample.entity.Product;
import com.example.inventoryexample.entity.Stock;
import com.example.inventoryexample.entity.Supplier;
import com.example.inventoryexample.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * White Box Testing untuk StockService
 * 
 * Teknik yang digunakan:
 * 1. Statement Coverage - Setiap baris kode dieksekusi
 * 2. Branch Coverage - Setiap cabang kondisional diuji
 * 3. Path Coverage - Semua jalur eksekusi
 * 4. Boundary Value Analysis - Nilai batas untuk stok
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("White Box Testing - StockService")
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock stock;
    private Product product;
    private Category category;
    private Supplier supplier;
    private StockDto stockDto;

    @BeforeEach
    void setUp() {
        // Setup Category
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        // Setup Supplier
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");

        // Setup Product
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("SKU-001");
        product.setCategory(category);
        product.setSupplier(supplier);

        // Setup Stock
        stock = new Stock();
        stock.setId(1L);
        stock.setQuantity(100);
        stock.setMinimumStock(10);
        stock.setProduct(product);
        stock.setLastRestockDate(LocalDateTime.now());

        // Setup StockDto
        stockDto = new StockDto();
        stockDto.setQuantity(150);
        stockDto.setMinimumStock(20);
    }

    @Nested
    @DisplayName("Test getAllStocks() - Statement Coverage")
    class GetAllStocksTest {

        @Test
        @DisplayName("Should return all stocks")
        void getAllStocks_WhenStocksExist_ReturnsStockDtoList() {
            // Arrange
            when(stockRepository.findAll()).thenReturn(Arrays.asList(stock));

            // Act
            List<StockDto> result = stockService.getAllStocks();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).getProductName());
            verify(stockRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no stocks")
        void getAllStocks_WhenNoStocks_ReturnsEmptyList() {
            // Arrange
            when(stockRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<StockDto> result = stockService.getAllStocks();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test getStockById() - Branch Coverage")
    class GetStockByIdTest {

        @Test
        @DisplayName("Should return stock when found")
        void getStockById_WhenStockExists_ReturnsStockDto() {
            // Arrange
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(100, result.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when stock not found")
        void getStockById_WhenStockNotFound_ThrowsException() {
            // Arrange
            when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> stockService.getStockById(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }
    }

    @Nested
    @DisplayName("Test getStockByProductId() - Branch Coverage")
    class GetStockByProductIdTest {

        @Test
        @DisplayName("Should return stock when product has stock")
        void getStockByProductId_WhenExists_ReturnsStockDto() {
            // Arrange
            when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockByProductId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getProductId());
        }

        @Test
        @DisplayName("Should throw exception when product stock not found")
        void getStockByProductId_WhenNotFound_ThrowsException() {
            // Arrange
            when(stockRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> stockService.getStockByProductId(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }
    }

    @Nested
    @DisplayName("Test getLowStocks() - Statement Coverage")
    class GetLowStocksTest {

        @Test
        @DisplayName("Should return low stock items")
        void getLowStocks_WhenLowStocksExist_ReturnsStockDtoList() {
            // Arrange
            stock.setQuantity(5); // Below minimum
            when(stockRepository.findLowStocks()).thenReturn(Arrays.asList(stock));

            // Act
            List<StockDto> result = stockService.getLowStocks();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getIsLowStock());
        }

        @Test
        @DisplayName("Should return empty list when no low stocks")
        void getLowStocks_WhenNoLowStocks_ReturnsEmptyList() {
            // Arrange
            when(stockRepository.findLowStocks()).thenReturn(Collections.emptyList());

            // Act
            List<StockDto> result = stockService.getLowStocks();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test getOutOfStocks() - Statement Coverage")
    class GetOutOfStocksTest {

        @Test
        @DisplayName("Should return out of stock items")
        void getOutOfStocks_WhenOutOfStocksExist_ReturnsStockDtoList() {
            // Arrange
            stock.setQuantity(0);
            when(stockRepository.findOutOfStocks()).thenReturn(Arrays.asList(stock));

            // Act
            List<StockDto> result = stockService.getOutOfStocks();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(0, result.get(0).getQuantity());
        }

        @Test
        @DisplayName("Should return empty list when no out of stock items")
        void getOutOfStocks_WhenNoOutOfStocks_ReturnsEmptyList() {
            // Arrange
            when(stockRepository.findOutOfStocks()).thenReturn(Collections.emptyList());

            // Act
            List<StockDto> result = stockService.getOutOfStocks();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test updateStock() - Path Coverage")
    class UpdateStockTest {

        @Test
        @DisplayName("Should update stock when found")
        void updateStock_WhenStockExists_UpdatesSuccessfully() {
            // Arrange
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            // Act
            StockDto result = stockService.updateStock(1L, stockDto);

            // Assert
            assertNotNull(result);
            verify(stockRepository, times(1)).save(stock);
        }

        @Test
        @DisplayName("Should throw exception when stock not found")
        void updateStock_WhenStockNotFound_ThrowsException() {
            // Arrange
            when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> stockService.updateStock(999L, stockDto)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }

        @Test
        @DisplayName("Should update both quantity and minimumStock")
        void updateStock_ShouldUpdateAllFields() {
            // Arrange
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> {
                Stock savedStock = invocation.getArgument(0);
                return savedStock;
            });

            // Act
            stockService.updateStock(1L, stockDto);

            // Assert
            verify(stockRepository).save(argThat(s -> 
                s.getQuantity() == 150 && s.getMinimumStock() == 20
            ));
        }
    }

    @Nested
    @DisplayName("Test addStock() - Path Coverage")
    class AddStockTest {

        @Test
        @DisplayName("Should add stock when product exists")
        void addStock_WhenProductExists_IncreasesQuantity() {
            // Arrange
            int initialQuantity = stock.getQuantity();
            int amountToAdd = 50;
            
            when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            // Act
            StockDto result = stockService.addStock(1L, amountToAdd);

            // Assert
            assertNotNull(result);
            assertEquals(initialQuantity + amountToAdd, stock.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when product stock not found")
        void addStock_WhenProductNotFound_ThrowsException() {
            // Arrange
            when(stockRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, 
                () -> stockService.addStock(999L, 50));
        }

        @Test
        @DisplayName("Should update lastRestockDate when adding stock")
        void addStock_ShouldUpdateLastRestockDate() {
            // Arrange
            when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            // Act
            stockService.addStock(1L, 10);

            // Assert
            assertNotNull(stock.getLastRestockDate());
        }
    }

    @Nested
    @DisplayName("Test reduceStock() - Path & Exception Coverage")
    class ReduceStockTest {

        @Test
        @DisplayName("Should reduce stock when sufficient quantity")
        void reduceStock_WhenSufficientQuantity_DecreasesSuccessfully() {
            // Arrange
            stock.setQuantity(100);
            int amountToReduce = 30;
            
            when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            // Act
            StockDto result = stockService.reduceStock(1L, amountToReduce);

            // Assert
            assertNotNull(result);
            assertEquals(70, stock.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void reduceStock_WhenProductNotFound_ThrowsException() {
            // Arrange
            when(stockRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, 
                () -> stockService.reduceStock(999L, 10));
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock")
        void reduceStock_WhenInsufficientStock_ThrowsException() {
            // Arrange
            stock.setQuantity(10);
            when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));

            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> stockService.reduceStock(1L, 50));
        }

        @Test
        @DisplayName("Should reduce to zero when amount equals quantity")
        void reduceStock_WhenAmountEqualsQuantity_ReducesToZero() {
            // Arrange
            stock.setQuantity(50);
            when(stockRepository.findByProductId(1L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            // Act
            stockService.reduceStock(1L, 50);

            // Assert
            assertEquals(0, stock.getQuantity());
        }
    }

    @Nested
    @DisplayName("Test convertToDto() - Null Safety Coverage")
    class ConvertToDtoTest {

        @Test
        @DisplayName("Should handle null category gracefully")
        void convertToDto_WhenCategoryNull_ReturnsDefaultValue() {
            // Arrange
            product.setCategory(null);
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockById(1L);

            // Assert
            assertNotNull(result);
            assertEquals("-", result.getCategoryName());
        }

        @Test
        @DisplayName("Should handle null supplier gracefully")
        void convertToDto_WhenSupplierNull_ReturnsDefaultValue() {
            // Arrange
            product.setSupplier(null);
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockById(1L);

            // Assert
            assertNotNull(result);
            assertEquals("-", result.getSupplierName());
        }

        @Test
        @DisplayName("Should convert all fields correctly")
        void convertToDto_ShouldConvertAllFields() {
            // Arrange
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockById(1L);

            // Assert
            assertEquals(1L, result.getId());
            assertEquals(1L, result.getProductId());
            assertEquals("Test Product", result.getProductName());
            assertEquals("SKU-001", result.getProductSku());
            assertEquals("Electronics", result.getCategoryName());
            assertEquals("Test Supplier", result.getSupplierName());
            assertEquals(100, result.getQuantity());
            assertEquals(10, result.getMinimumStock());
            assertNotNull(result.getLastRestockDate());
            assertFalse(result.getIsLowStock()); // 100 > 10
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should correctly identify low stock at boundary")
        void isLowStock_AtBoundary_ReturnsCorrectValue() {
            // Arrange - At boundary (quantity == minimumStock)
            stock.setQuantity(10);
            stock.setMinimumStock(10);
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockById(1L);

            // Assert
            assertTrue(result.getIsLowStock());
        }

        @Test
        @DisplayName("Should correctly identify not low stock just above boundary")
        void isLowStock_JustAboveBoundary_ReturnsNotLowStock() {
            // Arrange - Just above boundary (quantity = minimumStock + 1)
            stock.setQuantity(11);
            stock.setMinimumStock(10);
            when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

            // Act
            StockDto result = stockService.getStockById(1L);

            // Assert
            assertFalse(result.getIsLowStock());
        }
    }
}
