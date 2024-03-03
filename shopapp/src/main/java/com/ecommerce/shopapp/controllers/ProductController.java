package com.ecommerce.shopapp.controllers;


import com.ecommerce.shopapp.dtos.ProductDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {


    @GetMapping("")
    public ResponseEntity<?> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        return ResponseEntity.ok("Products");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable Integer id
    ){
        return ResponseEntity.ok("Product have id = "+id);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertProduct(
            @Valid @ModelAttribute @RequestBody ProductDto productDto,
//            @RequestPart("file") MultipartFile file,
            BindingResult result
            ){

        try{
            if (result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            List <MultipartFile> files = productDto.getFiles();
            files = files == null ? new ArrayList<>():files;
            for(MultipartFile file : files){
                if (file.getSize() == 0){
                    continue;
                }
                if (file.getSize() > 10 * 1024 * 1024){
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large! Maximum is 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                String filename = storeFile(file);
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Product is inserted successful");
    }

    private String storeFile(MultipartFile file) throws IOException{
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //Them UUID vào trước tên file
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        Path uploadDir = Paths.get("upload");

        //Kiểm tra xem thư mục có tồn tại chưa và sẽ tạo nếu chưa có
        if (!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        //Đường dẫn đích
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        //Copy file vào đường dẫn đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Integer id){
        return ResponseEntity.ok("Update product by id = "+ id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id){
        return ResponseEntity.ok("Delete product by id = "+ id);
    }

}
