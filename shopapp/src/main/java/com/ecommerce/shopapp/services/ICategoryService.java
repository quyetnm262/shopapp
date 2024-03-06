package com.ecommerce.shopapp.services;

import com.ecommerce.shopapp.dtos.CategoryDto;
import com.ecommerce.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDto categoryDto);

    Category getCategory(Long categoryId);

    List<Category> getAllCategory();

    Category updateCategory(Long categoryId, CategoryDto categoryDto);

    void deleteCategory(Long categoryId);
}
