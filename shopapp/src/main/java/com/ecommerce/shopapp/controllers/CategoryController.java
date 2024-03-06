package com.ecommerce.shopapp.controllers;

import com.ecommerce.shopapp.dtos.CategoryDto;
import com.ecommerce.shopapp.models.Category;
import com.ecommerce.shopapp.services.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@Validated
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories") //http://localhost:8081/api/v1/categories
public class CategoryController {

    private final ICategoryService iCategoryService;

    @GetMapping("") //http://localhost:8081/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getAllCategory(

//            @RequestParam("page") int page,
//            @RequestParam("limit") int limit
    ){

        List<Category> categories = iCategoryService.getAllCategory();

        return ResponseEntity.ok(categories);
    }

    @PostMapping("")
    public ResponseEntity<?> insertCategory(
            @Valid @RequestBody CategoryDto categoryDto,
            BindingResult result){
        if (result.hasErrors()){
            List<String> errorMessage = result.getFieldErrors()
                    .stream().
                    map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        iCategoryService.createCategory(categoryDto);

        return ResponseEntity.ok("Insert category with name = "+categoryDto.getName());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @Valid @PathVariable("id") Long categoryId,
            @RequestBody CategoryDto categoryDto){
        Category category = iCategoryService.updateCategory(categoryId, categoryDto);

        return ResponseEntity.ok(category);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable("id") Long categoryId){
        iCategoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Delete category by id = "+ categoryId);
    }
}
