package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.ProductDto;
import com.example.inventoryexample.entity.Category;
import com.example.inventoryexample.entity.Product;
import com.example.inventoryexample.entity.Stock;
import com.example.inventoryexample.entity.Supplier;
import com.example.inventoryexample.repository.CategoryRepository;
import com.example.inventoryexample.repository.ProductRepository;
import com.example.inventoryexample.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * White Box Testing untuk ProductService
 * 
 * Teknik yang digunakan:
 * 1. Statement Coverage - Memastikan semua statement dieksekusi
 * 2. Branch Coverage - Menguji semua cabang kondisi
 * 3. Path Coverage - Menguji semua jalur eksekusi
 * 4. Exception Testing - Menguji penanganan exception
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("White Box Testing - ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private Category category;
    private Supplier supplier;
    private Stock stock;

    @BeforeEach
    void setUp() {
        // Setup Category
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        // Setup Supplier
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");

        // Setup Stock
        stock = new Stock();
        stock.setId(1L);
        stock.setQuantity(100);
        stock.setMinimumStock(10);

        // Setup Product
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("SKU-001");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setStock(stock);
        stock.setProduct(product);

        // Setup ProductDto
        productDto = new ProductDto();
        productDto.setName("New Product");
        productDto.setDescription("New Description");
        productDto.setPrice(new BigDecimal("149.99"));
        productDto.setCategoryId(1L);
        productDto.setSupplierId(1L);
        productDto.setStockQuantity(50);
        productDto.setMinimumStock(5);
    }

    @Nested
    @DisplayName("Test getAllProducts() - Statement Coverage")
    class GetAllProductsTest {

        @Test
        @DisplayName("Should return list of products when products exist")
        void getAllProducts_WhenProductsExist_ReturnsProductDtoList() {
            // Arrange
            when(productRepository.findAll()).thenReturn(Arrays.asList(product));

            // Act
            List<ProductDto> result = productService.getAllProducts();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).getName());
            verify(productRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no products exist")
        void getAllProducts_WhenNoProducts_ReturnsEmptyList() {
            // Arrange
            when(productRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<ProductDto> result = productService.getAllProducts();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test getProductById() - Branch Coverage")
    class GetProductByIdTest {

        @Test
        @DisplayName("Should return product when found (success path)")
        void getProductById_WhenProductExists_ReturnsProductDto() {
            // Arrange - Path: product found
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            ProductDto result = productService.getProductById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Product", result.getName());
        }

        @Test
        @DisplayName("Should throw exception when product not found (exception path)")
        void getProductById_WhenProductNotFound_ThrowsException() {
            // Arrange - Path: product not found
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.getProductById(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }
    }

    @Nested
    @DisplayName("Test createProduct() - Path Coverage")
    class CreateProductTest {

        @Test
        @DisplayName("Should create product when category and supplier exist")
        void createProduct_WhenValidInput_CreatesProduct() {
            // Arrange - Happy path
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product savedProduct = invocation.getArgument(0);
                savedProduct.setId(1L);
                return savedProduct;
            });

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            assertEquals("New Product", result.getName());
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void createProduct_WhenCategoryNotFound_ThrowsException() {
            // Arrange - Path: category not found
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.createProduct(productDto)
            );
            assertTrue(exception.getMessage().contains("Kategori"));
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void createProduct_WhenSupplierNotFound_ThrowsException() {
            // Arrange - Path: supplier not found
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.createProduct(productDto)
            );
            assertTrue(exception.getMessage().contains("Supplier"));
        }

        @Test
        @DisplayName("Should use default stock quantity when null")
        void createProduct_WhenStockQuantityNull_UsesDefaultValue() {
            // Arrange - Branch: stockQuantity == null
            productDto.setStockQuantity(null);
            productDto.setMinimumStock(null);
            
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product savedProduct = invocation.getArgument(0);
                savedProduct.setId(1L);
                return savedProduct;
            });

            // Act
            ProductDto result = productService.createProduct(productDto);

            // Assert
            assertNotNull(result);
            verify(productRepository).save(argThat(p -> 
                p.getStock().getQuantity() == 0 && 
                p.getStock().getMinimumStock() == 10
            ));
        }

        @Test
        @DisplayName("Should use provided stock quantity when not null")
        void createProduct_WhenStockQuantityProvided_UsesProvidedValue() {
            // Arrange - Branch: stockQuantity != null
            productDto.setStockQuantity(75);
            productDto.setMinimumStock(15);
            
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product savedProduct = invocation.getArgument(0);
                savedProduct.setId(1L);
                return savedProduct;
            });

            // Act
            productService.createProduct(productDto);

            // Assert
            verify(productRepository).save(argThat(p -> 
                p.getStock().getQuantity() == 75 && 
                p.getStock().getMinimumStock() == 15
            ));
        }
    }

    @Nested
    @DisplayName("Test updateProduct() - All Branches")
    class UpdateProductTest {

        @Test
        @DisplayName("Should update product when all entities exist")
        void updateProduct_WhenAllValid_UpdatesProduct() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // Act
            ProductDto result = productService.updateProduct(1L, productDto);

            // Assert
            assertNotNull(result);
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void updateProduct_WhenProductNotFound_ThrowsException() {
            // Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, 
                () -> productService.updateProduct(999L, productDto));
        }

        @Test
        @DisplayName("Should throw exception when category not found during update")
        void updateProduct_WhenCategoryNotFound_ThrowsException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, 
                () -> productService.updateProduct(1L, productDto));
        }

        @Test
        @DisplayName("Should throw exception when supplier not found during update")
        void updateProduct_WhenSupplierNotFound_ThrowsException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, 
                () -> productService.updateProduct(1L, productDto));
        }
    }

    @Nested
    @DisplayName("Test deleteProduct() - Branch Coverage")
    class DeleteProductTest {

        @Test
        @DisplayName("Should delete product when exists")
        void deleteProduct_WhenProductExists_DeletesSuccessfully() {
            // Arrange - Path: product exists
            when(productRepository.existsById(1L)).thenReturn(true);
            doNothing().when(productRepository).deleteById(1L);

            // Act
            productService.deleteProduct(1L);

            // Assert
            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void deleteProduct_WhenProductNotFound_ThrowsException() {
            // Arrange - Path: product not found
            when(productRepository.existsById(anyLong())).thenReturn(false);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.deleteProduct(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
            verify(productRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Test searchProductsByName() - Statement Coverage")
    class SearchProductsTest {

        @Test
        @DisplayName("Should return matching products")
        void searchProductsByName_WhenMatches_ReturnsProducts() {
            // Arrange
            when(productRepository.findByNameContainingIgnoreCase("Test"))
                .thenReturn(Arrays.asList(product));

            // Act
            List<ProductDto> result = productService.searchProductsByName("Test");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no matches")
        void searchProductsByName_WhenNoMatches_ReturnsEmptyList() {
            // Arrange
            when(productRepository.findByNameContainingIgnoreCase("NonExistent"))
                .thenReturn(Collections.emptyList());

            // Act
            List<ProductDto> result = productService.searchProductsByName("NonExistent");

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test getProductsByCategory() & getProductsBySupplier()")
    class FilterProductsTest {

        @Test
        @DisplayName("Should return products by category")
        void getProductsByCategory_ReturnsFilteredProducts() {
            // Arrange
            when(productRepository.findByCategoryId(1L))
                .thenReturn(Arrays.asList(product));

            // Act
            List<ProductDto> result = productService.getProductsByCategory(1L);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Electronics", result.get(0).getCategoryName());
        }

        @Test
        @DisplayName("Should return products by supplier")
        void getProductsBySupplier_ReturnsFilteredProducts() {
            // Arrange
            when(productRepository.findBySupplierId(1L))
                .thenReturn(Arrays.asList(product));

            // Act
            List<ProductDto> result = productService.getProductsBySupplier(1L);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Test Supplier", result.get(0).getSupplierName());
        }
    }

    @Nested
    @DisplayName("Test convertToDto() - Statement Coverage for null checks")
    class ConvertToDtoTest {

        @Test
        @DisplayName("Should handle product with null stock")
        void convertToDto_WhenStockIsNull_HandlesGracefully() {
            // Arrange
            product.setStock(null);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            ProductDto result = productService.getProductById(1L);

            // Assert
            assertNotNull(result);
            assertNull(result.getStockQuantity());
            assertNull(result.getMinimumStock());
            assertNull(result.getIsLowStock());
        }

        @Test
        @DisplayName("Should convert all fields when stock exists")
        void convertToDto_WhenStockExists_ConvertsAllFields() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            ProductDto result = productService.getProductById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(100, result.getStockQuantity());
            assertEquals(10, result.getMinimumStock());
            assertNotNull(result.getIsLowStock());
        }
    }
}
