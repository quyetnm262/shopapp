package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.CategoryDto;
import com.ecommerce.shopapp.models.Category;
import com.ecommerce.shopapp.repositories.CategoryRepository;
import com.ecommerce.shopapp.services.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDto categoryDto) {

        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(()->new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category existingCategory = getCategory(categoryId);
        existingCategory.setName(categoryDto.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        //xoá cứng
        categoryRepository.deleteById(categoryId);

    }
}
