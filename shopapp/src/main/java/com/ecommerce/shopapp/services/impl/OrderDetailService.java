package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.OrderDetailDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.models.Order;
import com.ecommerce.shopapp.models.OrderDetail;
import com.ecommerce.shopapp.models.Product;
import com.ecommerce.shopapp.repositories.OrderDetailRepository;
import com.ecommerce.shopapp.repositories.OrderRepository;
import com.ecommerce.shopapp.repositories.ProductRepository;
import com.ecommerce.shopapp.responses.OrderDetailResponse;
import com.ecommerce.shopapp.services.IOrderDetailService;
import com.ecommerce.shopapp.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    private final ModelMapperUtils modelMapperUtils;

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDto orderDetailDto) throws Exception {

        Order order = orderRepository.findById(orderDetailDto.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail" +
                        " with order id = " + orderDetailDto.getOrderId() ));
        Product product = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product" +
                        " with product id = " + orderDetailDto.getProductId()));

        OrderDetail orderDetail = OrderDetail.builder()
                .price(orderDetailDto.getPrice())
                .numberOfProducts(orderDetailDto.getNumberOfProducts())
                .totalMoney(orderDetailDto.getTotalMoney())
                .color(orderDetailDto.getColor())
                .order(order)
                .product(product)
                .build();

        orderDetailRepository.save(orderDetail);

        return modelMapper.map(orderDetail,OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderDetailId) throws Exception {

        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail" +
                        " with id = " + orderDetailId));

        return modelMapper.map(orderDetail, OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long orderDetailId, OrderDetailDto orderDetailDto) throws Exception {

        OrderDetail existedOrderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(()->new DataNotFoundException("Cannot find order detail" +
                        "with id = "+ orderDetailId));
        Order existedOrder = orderRepository.findById(orderDetailDto.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail" +
                        " with order id = " + orderDetailDto.getOrderId() ));
        Product existedProduct = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product" +
                        " with product id = " + orderDetailDto.getProductId()));
        modelMapper.map(orderDetailDto,existedOrderDetail);
        existedOrderDetail.setOrder(existedOrder);
        existedOrderDetail.setProduct(existedProduct);

        return modelMapper.map(existedOrderDetail,OrderDetailResponse.class);
    }

    @Override
    public void deleteOrderDetail(Long orderDetailId) throws DataNotFoundException {
        //Xoá cứng
        OrderDetail existOrderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(()->new DataNotFoundException("Cannot find order detail" +
                        "with id = "+ orderDetailId));
        orderDetailRepository.delete(existOrderDetail);

    }

    @Override
    public List<OrderDetailResponse> getOrderDetails(Long orderId) {

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return ModelMapperUtils.mapAll(orderDetails, OrderDetailResponse.class);
    }
}
