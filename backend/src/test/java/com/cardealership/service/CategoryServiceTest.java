package com.cardealership.service;

import com.cardealership.entity.Category;
import com.cardealership.exception.DuplicateResourceException;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_ShouldReturnSavedCategory() {
        when(categoryRepository.existsByName("SUV")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(new Category(1L, "SUV", "Sport utility"));

        Category result = categoryService.createCategory("SUV", "Sport utility");

        assertThat(result.getName()).isEqualTo("SUV");
        verify(categoryRepository).save(any());
    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrow() {
        when(categoryRepository.existsByName("SUV")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory("SUV", null))
                .isInstanceOf(DuplicateResourceException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                new Category(1L, "SUV", null),
                new Category(2L, "Sedan", null)
        ));

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
    }

    @Test
    void deleteCategory_ShouldCallRepository() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_WhenNotFound_ShouldThrow() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(categoryRepository, never()).deleteById(any());
    }
}
