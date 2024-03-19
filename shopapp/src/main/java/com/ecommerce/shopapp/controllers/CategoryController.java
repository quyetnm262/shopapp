package com.ecommerce.shopapp.controllers;

import com.ecommerce.shopapp.dtos.CategoryDto;
import com.ecommerce.shopapp.models.Category;
import com.ecommerce.shopapp.responses.LoginResponse;
import com.ecommerce.shopapp.responses.UpdateCategoryResponse;
import com.ecommerce.shopapp.services.ICategoryService;
import com.ecommerce.shopapp.components.LocalizationUtils;
import com.ecommerce.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    private final LocalizationUtils localizationUtils;

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
    @Transactional
    public ResponseEntity<?> updateCategory(
            @Valid @PathVariable("id") Long categoryId,
            @RequestBody CategoryDto categoryDto){

        try{
            iCategoryService.updateCategory(categoryId, categoryDto);

            return ResponseEntity.ok(UpdateCategoryResponse.builder()
                    .message(localizationUtils
                            .getLocalizationMessage(MessageKeys.CATEGORY_UPDATE_SUCCESSFULLY))
                    .build());

        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizationMessage(MessageKeys.CATEGORY_UPDATE_FAILED, e.getMessage()))
                            .build()
            );
        }

    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteCategory(
            @PathVariable("id") Long categoryId){
        iCategoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Delete category by id = "+ categoryId);
    }
}
