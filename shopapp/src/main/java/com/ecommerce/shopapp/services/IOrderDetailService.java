package com.ecommerce.shopapp.services;

import com.ecommerce.shopapp.dtos.OrderDetailDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.responses.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {

    OrderDetailResponse createOrderDetail(OrderDetailDto orderDetailDto) throws Exception;

    OrderDetailResponse getOrderDetail(Long orderDetailId) throws Exception;

    OrderDetailResponse updateOrderDetail(Long orderDetailId, OrderDetailDto orderDetailDto) throws Exception;

    void deleteOrderDetail(Long orderDetailId) throws Exception;

    List<OrderDetailResponse> getOrderDetails(Long orderId);

}
