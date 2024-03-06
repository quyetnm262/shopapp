package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.ProductDto;
import com.ecommerce.shopapp.dtos.ProductImageDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.exceptions.InvalidParamException;
import com.ecommerce.shopapp.models.Category;
import com.ecommerce.shopapp.models.Product;
import com.ecommerce.shopapp.models.ProductImage;
import com.ecommerce.shopapp.repositories.CategoryRepository;
import com.ecommerce.shopapp.repositories.ProductImageRepository;
import com.ecommerce.shopapp.repositories.ProductRepository;
import com.ecommerce.shopapp.responses.ProductResponse;
import com.ecommerce.shopapp.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ecommerce.shopapp.models.ProductImage.MAXIMUM_IMAGES_PER_PRODUCT;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductImageRepository productImageRepository;
    @Override
    public Product createProduct(ProductDto productDto) throws DataNotFoundException{
        Category existedCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(()->
                        new DataNotFoundException("Cannot find the category" +
                                "id = "+productDto.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .thumbnail(productDto.getThumbnail())
                .description(productDto.getDescription())
                .category(existedCategory)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(()->
                        new DataNotFoundException("Cannot found product with id = "+productId));
    }

    @Override
    public Page<ProductResponse> getAllProduct(PageRequest pageRequest) {

        //Lấy danh sách sản phẩm phân theo trang

        return productRepository.findAll(pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long id, ProductDto productDto) throws DataNotFoundException {

        Category existedCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(()->
                        new DataNotFoundException("Cannot find the category" +
                                "id = "+productDto.getCategoryId()));


        Product existedProduct = getProductById(id);
        Product updatedProduct = Product.builder()
                .id(existedProduct.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .thumbnail(productDto.getThumbnail())
                .description(productDto.getDescription())
                .category(existedCategory)
                .build();
        updatedProduct.setCreatedAt(existedProduct.getCreatedAt());

        return productRepository.save(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        Optional<Product> existedProduct = productRepository.findById(productId);
        existedProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsProductByName(String name) {
        return productRepository.existsByName(name);
    }

    public ProductImage createProductImage(
            ProductImageDto productImageDto) throws DataNotFoundException, InvalidParamException {

        Product existedProduct = productRepository
                .findById(productImageDto.getProductId())
                .orElseThrow(()-> new DataNotFoundException("Cannot found product with id = " +
                        productImageDto.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existedProduct)
                .imageUrl(productImageDto.getImageUrl())
                .build();
        //Khong cho insert qua 5 anh
        int size = productImageRepository.findByProductId(existedProduct.getId()).size();
        if (size >= MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException("Number of images must be <= 5");

        }
        return productImageRepository.save(newProductImage);

    }
}
