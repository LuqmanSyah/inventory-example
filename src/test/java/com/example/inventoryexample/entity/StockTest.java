package com.example.inventoryexample.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White Box Testing untuk entity Stock
 * 
 * Teknik yang digunakan:
 * 1. Statement Coverage - Menguji setiap statement dalam method
 * 2. Branch Coverage - Menguji setiap cabang kondisi (if/else)
 * 3. Path Coverage - Menguji semua jalur eksekusi yang mungkin
 * 4. Boundary Value Analysis - Menguji nilai batas
 */
@DisplayName("White Box Testing - Stock Entity")
class StockTest {

    private Stock stock;
    private Product product;

    @BeforeEach
    void setUp() {
        stock = new Stock();
        stock.setId(1L);
        stock.setQuantity(100);
        stock.setMinimumStock(10);
        
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        stock.setProduct(product);
    }

    @Nested
    @DisplayName("Test isLowStock() - Branch Coverage")
    class IsLowStockTest {

        @Test
        @DisplayName("Should return true when quantity equals minimumStock (boundary)")
        void isLowStock_WhenQuantityEqualsMinimum_ReturnsTrue() {
            // Arrange - Testing boundary condition (quantity == minimumStock)
            stock.setQuantity(10);
            stock.setMinimumStock(10);

            // Act
            boolean result = stock.isLowStock();

            // Assert
            assertTrue(result, "Should be low stock when quantity equals minimum");
        }

        @Test
        @DisplayName("Should return true when quantity is less than minimumStock")
        void isLowStock_WhenQuantityLessThanMinimum_ReturnsTrue() {
            // Arrange - Testing branch: quantity < minimumStock
            stock.setQuantity(5);
            stock.setMinimumStock(10);

            // Act
            boolean result = stock.isLowStock();

            // Assert
            assertTrue(result, "Should be low stock when quantity is below minimum");
        }

        @Test
        @DisplayName("Should return false when quantity is greater than minimumStock")
        void isLowStock_WhenQuantityGreaterThanMinimum_ReturnsFalse() {
            // Arrange - Testing branch: quantity > minimumStock
            stock.setQuantity(100);
            stock.setMinimumStock(10);

            // Act
            boolean result = stock.isLowStock();

            // Assert
            assertFalse(result, "Should not be low stock when quantity exceeds minimum");
        }

        @Test
        @DisplayName("Should return true when quantity is zero")
        void isLowStock_WhenQuantityIsZero_ReturnsTrue() {
            // Arrange - Edge case: empty stock
            stock.setQuantity(0);
            stock.setMinimumStock(10);

            // Act
            boolean result = stock.isLowStock();

            // Assert
            assertTrue(result, "Should be low stock when quantity is zero");
        }

        @Test
        @DisplayName("Should handle zero minimumStock correctly")
        void isLowStock_WhenMinimumStockIsZero_HandlesCorrectly() {
            // Arrange - Edge case: minimumStock = 0
            stock.setQuantity(0);
            stock.setMinimumStock(0);

            // Act
            boolean result = stock.isLowStock();

            // Assert
            assertTrue(result, "Should be low stock when both are zero");
        }
    }

    @Nested
    @DisplayName("Test addStock() - Statement & Path Coverage")
    class AddStockTest {

        @Test
        @DisplayName("Should add positive amount to quantity")
        void addStock_WithPositiveAmount_IncreasesQuantity() {
            // Arrange
            int initialQuantity = stock.getQuantity();
            int amountToAdd = 50;

            // Act
            stock.addStock(amountToAdd);

            // Assert
            assertEquals(initialQuantity + amountToAdd, stock.getQuantity());
            assertNotNull(stock.getLastRestockDate(), "Last restock date should be set");
            assertNotNull(stock.getUpdatedAt(), "Updated at should be set");
        }

        @Test
        @DisplayName("Should set lastRestockDate when adding stock")
        void addStock_ShouldSetLastRestockDate() {
            // Arrange
            assertNull(stock.getLastRestockDate());

            // Act
            stock.addStock(10);

            // Assert
            assertNotNull(stock.getLastRestockDate());
        }

        @Test
        @DisplayName("Should handle adding zero amount")
        void addStock_WithZeroAmount_QuantityUnchanged() {
            // Arrange
            int initialQuantity = stock.getQuantity();

            // Act
            stock.addStock(0);

            // Assert
            assertEquals(initialQuantity, stock.getQuantity());
        }

        @Test
        @DisplayName("Should handle adding large amount (overflow test)")
        void addStock_WithLargeAmount_HandlesCorrectly() {
            // Arrange
            stock.setQuantity(Integer.MAX_VALUE - 100);

            // Act & Assert - Testing potential overflow
            stock.addStock(50);
            assertEquals(Integer.MAX_VALUE - 50, stock.getQuantity());
        }
    }

    @Nested
    @DisplayName("Test reduceStock() - Branch & Exception Coverage")
    class ReduceStockTest {

        @Test
        @DisplayName("Should reduce stock when sufficient quantity exists")
        void reduceStock_WhenSufficientQuantity_ReducesSuccessfully() {
            // Arrange - Path: quantity >= amount (true branch)
            stock.setQuantity(100);
            int amountToReduce = 30;

            // Act
            stock.reduceStock(amountToReduce);

            // Assert
            assertEquals(70, stock.getQuantity());
            assertNotNull(stock.getUpdatedAt());
        }

        @Test
        @DisplayName("Should reduce stock to zero when amount equals quantity")
        void reduceStock_WhenAmountEqualsQuantity_ReducesToZero() {
            // Arrange - Boundary: quantity == amount
            stock.setQuantity(50);

            // Act
            stock.reduceStock(50);

            // Assert
            assertEquals(0, stock.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when insufficient quantity")
        void reduceStock_WhenInsufficientQuantity_ThrowsException() {
            // Arrange - Path: quantity < amount (false branch - exception)
            stock.setQuantity(10);
            int amountToReduce = 50;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> stock.reduceStock(amountToReduce)
            );
            assertEquals("Stok tidak mencukupi", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when reducing from zero stock")
        void reduceStock_WhenZeroStock_ThrowsException() {
            // Arrange - Edge case: reducing from empty stock
            stock.setQuantity(0);

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> stock.reduceStock(1)
            );
        }

        @Test
        @DisplayName("Should handle reducing zero amount")
        void reduceStock_WithZeroAmount_QuantityUnchanged() {
            // Arrange
            int initialQuantity = stock.getQuantity();

            // Act
            stock.reduceStock(0);

            // Assert
            assertEquals(initialQuantity, stock.getQuantity());
        }

        @Test
        @DisplayName("Should update timestamp after successful reduction")
        void reduceStock_WhenSuccessful_UpdatesTimestamp() {
            // Arrange
            stock.setQuantity(100);
            stock.setUpdatedAt(null);

            // Act
            stock.reduceStock(10);

            // Assert
            assertNotNull(stock.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test Lifecycle Callbacks - Statement Coverage")
    class LifecycleCallbacksTest {

        @Test
        @DisplayName("onCreate should set updatedAt")
        void onCreate_ShouldSetUpdatedAt() {
            // Arrange
            Stock newStock = new Stock();
            assertNull(newStock.getUpdatedAt());

            // Act - Simulate @PrePersist
            newStock.onCreate();

            // Assert
            assertNotNull(newStock.getUpdatedAt());
        }

        @Test
        @DisplayName("onUpdate should set updatedAt")
        void onUpdate_ShouldSetUpdatedAt() {
            // Arrange
            Stock existingStock = new Stock();
            existingStock.setUpdatedAt(null);

            // Act - Simulate @PreUpdate
            existingStock.onUpdate();

            // Assert
            assertNotNull(existingStock.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should handle minimum valid quantity (0)")
        void quantity_MinimumValue_HandledCorrectly() {
            stock.setQuantity(0);
            assertEquals(0, stock.getQuantity());
            assertTrue(stock.isLowStock());
        }

        @Test
        @DisplayName("Should handle quantity just above minimum stock")
        void quantity_JustAboveMinimumStock_NotLowStock() {
            stock.setMinimumStock(10);
            stock.setQuantity(11);
            assertFalse(stock.isLowStock());
        }

        @Test
        @DisplayName("Should handle quantity just at minimum stock")
        void quantity_AtMinimumStock_IsLowStock() {
            stock.setMinimumStock(10);
            stock.setQuantity(10);
            assertTrue(stock.isLowStock());
        }

        @Test
        @DisplayName("Should handle quantity just below minimum stock")
        void quantity_JustBelowMinimumStock_IsLowStock() {
            stock.setMinimumStock(10);
            stock.setQuantity(9);
            assertTrue(stock.isLowStock());
        }
    }
}
