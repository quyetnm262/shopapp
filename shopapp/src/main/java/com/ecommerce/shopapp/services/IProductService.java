package com.ecommerce.shopapp.services;

import com.ecommerce.shopapp.dtos.ProductDto;
import com.ecommerce.shopapp.dtos.ProductImageDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.exceptions.InvalidParamException;
import com.ecommerce.shopapp.models.Product;
import com.ecommerce.shopapp.models.ProductImage;
import com.ecommerce.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDto productDto) throws Exception;

    Product getProductById(Long productId) throws Exception;
    List<Product> getProductByIds(List<Long> productId) throws Exception;

    Page<ProductResponse> getAllProduct(String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct(Long id, ProductDto productDto) throws Exception;

    void deleteProduct(Long productId);

    boolean existsProductByName(String name);

    ProductImage createProductImage(ProductImageDto productImageDto) throws Exception;


}
