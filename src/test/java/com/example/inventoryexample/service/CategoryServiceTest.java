package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.CategoryDto;
import com.example.inventoryexample.entity.Category;
import com.example.inventoryexample.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * White Box Testing untuk CategoryService
 * 
 * Teknik yang digunakan:
 * 1. Statement Coverage - Setiap statement dieksekusi
 * 2. Branch Coverage - Setiap cabang kondisional diuji
 * 3. Path Coverage - Semua jalur eksekusi
 * 4. Condition Coverage - Kondisi kompleks diuji
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("White Box Testing - CategoryService")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        // Setup Category entity
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        // Setup CategoryDto
        categoryDto = new CategoryDto();
        categoryDto.setName("New Category");
        categoryDto.setDescription("New Description");
    }

    @Nested
    @DisplayName("Test getAllCategories() - Statement Coverage")
    class GetAllCategoriesTest {

        @Test
        @DisplayName("Should return list of categories when exist")
        void getAllCategories_WhenCategoriesExist_ReturnsCategoryDtoList() {
            // Arrange
            when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

            // Act
            List<CategoryDto> result = categoryService.getAllCategories();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Electronics", result.get(0).getName());
            verify(categoryRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no categories")
        void getAllCategories_WhenNoCategories_ReturnsEmptyList() {
            // Arrange
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<CategoryDto> result = categoryService.getAllCategories();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test getCategoryById() - Branch Coverage")
    class GetCategoryByIdTest {

        @Test
        @DisplayName("Should return category when found (success path)")
        void getCategoryById_WhenCategoryExists_ReturnsCategoryDto() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // Act
            CategoryDto result = categoryService.getCategoryById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Electronics", result.getName());
        }

        @Test
        @DisplayName("Should throw exception when category not found (exception path)")
        void getCategoryById_WhenCategoryNotFound_ThrowsException() {
            // Arrange
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.getCategoryById(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }
    }

    @Nested
    @DisplayName("Test createCategory() - Branch Coverage")
    class CreateCategoryTest {

        @Test
        @DisplayName("Should create category when name is unique")
        void createCategory_WhenNameUnique_CreatesSuccessfully() {
            // Arrange - Path: name doesn't exist
            when(categoryRepository.existsByName(anyString())).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            // Act
            CategoryDto result = categoryService.createCategory(categoryDto);

            // Assert
            assertNotNull(result);
            assertEquals("New Category", result.getName());
            verify(categoryRepository, times(1)).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw exception when category name already exists")
        void createCategory_WhenNameExists_ThrowsException() {
            // Arrange - Path: name already exists
            when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.createCategory(categoryDto)
            );
            assertTrue(exception.getMessage().contains("sudah ada"));
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Test updateCategory() - Complete Path Coverage")
    class UpdateCategoryTest {

        @Test
        @DisplayName("Should update category when found and name unchanged")
        void updateCategory_WhenFoundAndNameUnchanged_UpdatesSuccessfully() {
            // Arrange - Path: category found, name same
            categoryDto.setName("Electronics"); // Same as existing
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            CategoryDto result = categoryService.updateCategory(1L, categoryDto);

            // Assert
            assertNotNull(result);
            verify(categoryRepository, times(1)).save(any(Category.class));
            // Should NOT check existsByName because name is same
            verify(categoryRepository, never()).existsByName(anyString());
        }

        @Test
        @DisplayName("Should update category when name changed and new name is unique")
        void updateCategory_WhenNameChangedAndUnique_UpdatesSuccessfully() {
            // Arrange - Path: category found, name changed, new name unique
            // Branch: !category.getName().equals(categoryDto.getName()) = true
            // Branch: categoryRepository.existsByName(...) = false
            categoryDto.setName("New Name"); // Different from existing "Electronics"
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName("New Name")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            CategoryDto result = categoryService.updateCategory(1L, categoryDto);

            // Assert
            assertNotNull(result);
            verify(categoryRepository, times(1)).existsByName("New Name");
            verify(categoryRepository, times(1)).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void updateCategory_WhenCategoryNotFound_ThrowsException() {
            // Arrange - Path: category not found
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.updateCategory(999L, categoryDto)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }

        @Test
        @DisplayName("Should throw exception when name changed to existing name")
        void updateCategory_WhenNameChangedToExisting_ThrowsException() {
            // Arrange - Path: category found, name changed, new name exists
            // Branch: !category.getName().equals(categoryDto.getName()) = true
            // Branch: categoryRepository.existsByName(...) = true
            categoryDto.setName("Existing Category");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName("Existing Category")).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.updateCategory(1L, categoryDto)
            );
            assertTrue(exception.getMessage().contains("sudah ada"));
            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("Condition Coverage: Test complex condition in updateCategory")
        void updateCategory_ConditionCoverage_AllCombinations() {
            // This tests the complex condition:
            // if (!category.getName().equals(categoryDto.getName()) && 
            //     categoryRepository.existsByName(categoryDto.getName()))

            // Case 1: name same (first condition false) - skip second condition
            categoryDto.setName("Electronics");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            
            assertDoesNotThrow(() -> categoryService.updateCategory(1L, categoryDto));
            verify(categoryRepository, never()).existsByName(anyString());

            // Reset
            reset(categoryRepository);

            // Case 2: name different, doesn't exist (first true, second false)
            categoryDto.setName("Brand New");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName("Brand New")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            
            assertDoesNotThrow(() -> categoryService.updateCategory(1L, categoryDto));

            // Reset
            reset(categoryRepository);

            // Case 3: name different, already exists (both true)
            categoryDto.setName("Duplicate");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByName("Duplicate")).thenReturn(true);
            
            assertThrows(RuntimeException.class, 
                () -> categoryService.updateCategory(1L, categoryDto));
        }
    }

    @Nested
    @DisplayName("Test deleteCategory() - Branch Coverage")
    class DeleteCategoryTest {

        @Test
        @DisplayName("Should delete category when exists")
        void deleteCategory_WhenCategoryExists_DeletesSuccessfully() {
            // Arrange - Path: category exists
            when(categoryRepository.existsById(1L)).thenReturn(true);
            doNothing().when(categoryRepository).deleteById(1L);

            // Act
            categoryService.deleteCategory(1L);

            // Assert
            verify(categoryRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void deleteCategory_WhenCategoryNotFound_ThrowsException() {
            // Arrange - Path: category not found
            when(categoryRepository.existsById(anyLong())).thenReturn(false);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.deleteCategory(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
            verify(categoryRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Test convertToDto() - Statement Coverage")
    class ConvertToDtoTest {

        @Test
        @DisplayName("Should convert all fields correctly")
        void convertToDto_ShouldConvertAllFields() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // Act
            CategoryDto result = categoryService.getCategoryById(1L);

            // Assert
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            assertEquals(category.getDescription(), result.getDescription());
        }

        @Test
        @DisplayName("Should handle null description")
        void convertToDto_WhenDescriptionNull_HandlesGracefully() {
            // Arrange
            category.setDescription(null);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // Act
            CategoryDto result = categoryService.getCategoryById(1L);

            // Assert
            assertNotNull(result);
            assertNull(result.getDescription());
        }
    }

    @Nested
    @DisplayName("Edge Cases & Boundary Tests")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle empty string name in create")
        void createCategory_WithEmptyName_BehaviorTest() {
            // Note: This tests the service behavior - validation should be at DTO/Controller level
            categoryDto.setName("");
            when(categoryRepository.existsByName("")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            // Act - Service doesn't validate, just processes
            CategoryDto result = categoryService.createCategory(categoryDto);

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle very long category name")
        void createCategory_WithLongName_BehaviorTest() {
            // Arrange
            String longName = "A".repeat(500);
            categoryDto.setName(longName);
            when(categoryRepository.existsByName(longName)).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
                Category saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            // Act
            CategoryDto result = categoryService.createCategory(categoryDto);

            // Assert
            assertEquals(longName, result.getName());
        }
    }
}
