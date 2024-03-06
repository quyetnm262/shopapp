package com.ecommerce.shopapp.repositories;

import com.ecommerce.shopapp.models.Order;
import com.ecommerce.shopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
