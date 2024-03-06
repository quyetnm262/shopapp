package com.ecommerce.shopapp.repositories;

import com.ecommerce.shopapp.models.Category;
import com.ecommerce.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String productName);

    Page<Product> findAll(Pageable pageable);
}
