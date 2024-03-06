package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.OrderDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.models.Order;
import com.ecommerce.shopapp.models.OrderStatus;
import com.ecommerce.shopapp.models.User;
import com.ecommerce.shopapp.repositories.OrderRepository;
import com.ecommerce.shopapp.repositories.UserRepository;
import com.ecommerce.shopapp.responses.OrderResponse;
import com.ecommerce.shopapp.services.IOrderService;
import com.ecommerce.shopapp.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final ModelMapperUtils modelMapperUtils;

    @Override
    public OrderResponse createOrder(OrderDto orderDto) throws Exception{

        //Kiểm tra userId đã tồn tại hay chưa
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(()->
                        new DataNotFoundException("Cannot found User with id = "+orderDto.getUserId()));
        //Convert Dto -> model
        //Dung model mapper
        modelMapper.typeMap(OrderDto.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDto,order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        //Kiem tra shipping date phai >= ngay tao order
        LocalDate shippingDate = orderDto.getShippingDate() == null ? LocalDate.now() : orderDto.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today");
        }
        order.setActive(true);
        order.setShippingDate(shippingDate);
        orderRepository.save(order);
        modelMapper.typeMap(Order.class, OrderResponse.class);
        OrderResponse orderResponse = new OrderResponse();
        modelMapper.map(order,orderResponse);
        return orderResponse;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new DateTimeException("Cannot find order with id = "+orderId));
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    public OrderResponse updateOrder(Long orderId, OrderDto orderDto) {
        Order existedOrder = orderRepository.findById(orderId)
                .orElseThrow(()->new DateTimeException("Cannot find order with id = "+orderId));
        User existedUser = userRepository.findById(orderDto.getUserId())
                .orElseThrow(()->new DateTimeException("Cannot find user with id = "+orderDto.getUserId()));

        modelMapper.typeMap(OrderDto.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        modelMapper.map(orderDto, existedOrder);
        existedOrder.setUser(existedUser);
        modelMapper.map(existedOrder, OrderResponse.class);
        return modelMapper.map(existedOrder, OrderResponse.class);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order existedOrder = orderRepository.findById(orderId).orElse(null);
        //Xoá cứng
//        if (existedOrder.isPresent()){
//            orderRepository.delete(existedOrder.get());
//        }
        //Xoá mềm
        if (existedOrder != null){
            existedOrder.setActive(false);
            orderRepository.save(existedOrder);
        }



    }

    @Override
    public List<OrderResponse> findOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return modelMapperUtils.mapAll(orders,OrderResponse.class);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return null;
    }

    @Override
    public List<OrderResponse> findOrderById(Long orderId) {
        return null;
    }

}
