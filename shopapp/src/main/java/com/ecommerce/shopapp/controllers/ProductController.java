package com.ecommerce.shopapp.controllers;


import com.ecommerce.shopapp.dtos.ProductDto;
import com.ecommerce.shopapp.dtos.ProductImageDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.exceptions.InvalidParamException;
import com.ecommerce.shopapp.models.Product;
import com.ecommerce.shopapp.models.ProductImage;
import com.ecommerce.shopapp.repositories.ProductRepository;
import com.ecommerce.shopapp.responses.ProductListResponse;
import com.ecommerce.shopapp.responses.ProductResponse;
import com.ecommerce.shopapp.services.IProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Objects;
import java.util.UUID;

import static com.ecommerce.shopapp.models.ProductImage.MAXIMUM_IMAGES_PER_PRODUCT;


@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService iProductService;


    @GetMapping("")
    public ResponseEntity<?> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit,
//                Sort.by("createdAt").descending());
                Sort.by("id").ascending());
        Page<ProductResponse> productPage = iProductService.getAllProduct(pageRequest);
        int totalPage = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        ProductListResponse listResponse = ProductListResponse.builder()
                .products(products)
                .totalPages(totalPage)
                .build();
        return ResponseEntity.ok(listResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long productId
    ){

        try {

            Product existedProduct = iProductService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existedProduct));

        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> getProductByImageName(
            @PathVariable("imageName") String imageName
    ){

        try {

            Path imagePath = Paths.get("upload/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping(value = "")
    public ResponseEntity<?> insertProduct(
            @Valid
            @RequestBody ProductDto productDto,
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
            Product newProduct = iProductService.createProduct(productDto);
            return ResponseEntity.ok(ProductResponse.fromProduct(newProduct));

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping(value = "upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @Valid
            @ModelAttribute("files") List<MultipartFile> files,
            @PathVariable("id") Long productId) {

        try{

            Product existedProduct = iProductService.getProductById(productId);
            files = files == null ? new ArrayList<>():files;
            if (files.size() > MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("Only load max 5 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
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
                ProductImage productImage = iProductService.createProductImage(
                        ProductImageDto.builder()
                                .productId(existedProduct.getId())
                                .imageUrl(filename)
                                .build());
                productImages.add(productImage);
            }

            return ResponseEntity.ok(productImages);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @RequestBody ProductDto productDto,
            BindingResult result){

        if (result.hasErrors()){
            List<String> errorMessage = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            Product updatedProduct = iProductService.updateProduct(productId, productDto);
            ProductResponse productResponse = ProductResponse.fromProduct(updatedProduct);
            return ResponseEntity.ok(productResponse);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private String storeFile(MultipartFile file) throws IOException{

        if (!isImageFile(file) || file.getOriginalFilename() == null){

            throw new IOException("Invalid image file format");

        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
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

    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

//    @PostMapping("/generateFakerProducts")
    public ResponseEntity<?> generateFakerProducts(){
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {
            String productName = faker.commerce().productName();
            if (iProductService.existsProductByName(productName)){
                continue;
            }
            ProductDto productDto = ProductDto.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10,90000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(1,3))
                    .build();

            try{

                iProductService.createProduct(productDto);

            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("OK");
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId) {
        try {

            iProductService.deleteProduct(productId);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("OK");
    }

}
