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

public interface IProductService {
    Product createProduct(ProductDto productDto) throws DataNotFoundException;

    Product getProductById(Long productId) throws DataNotFoundException;

    Page<ProductResponse> getAllProduct(PageRequest pageRequest);

    Product updateProduct(Long id, ProductDto productDto) throws DataNotFoundException;

    void deleteProduct(Long productId);

    boolean existsProductByName(String name);

    ProductImage createProductImage(ProductImageDto productImageDto) throws DataNotFoundException, InvalidParamException;


}
