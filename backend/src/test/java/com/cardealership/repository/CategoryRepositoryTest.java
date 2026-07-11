package com.cardealership.repository;

import com.cardealership.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveCategory_ShouldPersistWithGeneratedId() {
        Category category = new Category(null, "SUV", "Sport utility vehicles");
        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("SUV");
    }

    @Test
    void findByName_ShouldReturnCategory() {
        categoryRepository.save(new Category(null, "Sedan", "Four-door cars"));

        Optional<Category> found = categoryRepository.findByName("Sedan");

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Four-door cars");
    }

    @Test
    void findByName_WhenMissing_ShouldReturnEmpty() {
        Optional<Category> found = categoryRepository.findByName("NonExistent");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        categoryRepository.save(new Category(null, "SUV", null));
        categoryRepository.save(new Category(null, "Sedan", null));
        categoryRepository.save(new Category(null, "Truck", null));

        assertThat(categoryRepository.findAll()).hasSize(3);
    }
}
