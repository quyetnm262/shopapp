package com.ecommerce.shopapp.services;

import com.ecommerce.shopapp.dtos.OrderDto;
import com.ecommerce.shopapp.models.Order;
import com.ecommerce.shopapp.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDto orderDto) throws Exception;

    OrderResponse getOrder(Long orderId);

    OrderResponse updateOrder(Long orderId, OrderDto orderDto);

    void deleteOrder(Long orderId);

    List<OrderResponse> findOrdersByUserId(Long userId);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> findOrderById(Long orderId);

    Page<Order> getOrderByKeyword(String keyword, Pageable pageable);
}
