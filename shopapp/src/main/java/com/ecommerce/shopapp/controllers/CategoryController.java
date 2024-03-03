package com.ecommerce.shopapp.controllers;

import com.ecommerce.shopapp.dtos.CategoryDto;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@Validated
@RequestMapping("api/v1/categories") //http://localhost:8081/api/v1/categories
public class CategoryController {

    @GetMapping("") //http://localhost:8081/api/v1/categories?page=1&limit=10
    public ResponseEntity<String> getAllCategory(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){

        return ResponseEntity.ok("Chao ban");
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
        return ResponseEntity.ok("Insert category with name = "+categoryDto.getName());
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Integer id){
        return ResponseEntity.ok("Update category by id = "+ id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id){
        return ResponseEntity.ok("Delete category by id = "+ id);
    }
}
