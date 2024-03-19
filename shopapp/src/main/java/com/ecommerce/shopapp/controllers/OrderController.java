package com.ecommerce.shopapp.controllers;

import com.ecommerce.shopapp.dtos.OrderDto;
import com.ecommerce.shopapp.responses.OrderResponse;
import com.ecommerce.shopapp.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderDto orderDto,
            BindingResult result){
        try {

            if (result.hasErrors()){
                List<String> errorContent = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorContent);
            }

            OrderResponse orderResponse = orderService.createOrder(orderDto);

            return ResponseEntity.ok(orderResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrders(
            @Valid @PathVariable("user_id") Long userId){
        try {

            List<OrderResponse> orders = orderService.findOrdersByUserId(userId);
            return ResponseEntity.ok(orders);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/{order_id}")
    public ResponseEntity<?> getOrder(
            @Valid @PathVariable("order_id") Long orderId){
        try {

            OrderResponse orderResponse = orderService.getOrder(orderId);
            return ResponseEntity.ok(orderResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{order_id}")
    @Transactional
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable("order_id") Long order_id,
            @RequestBody OrderDto orderDto,
            BindingResult result
    ){
        try {

            if (result.hasErrors()){
                List<String> errorContent = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorContent);
            }
            OrderResponse orderResponse = orderService.updateOrder(order_id, orderDto);

            return ResponseEntity.ok(orderResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{order_id}")
    @Transactional
    public ResponseEntity<?> deleteOrder(
            @Valid @PathVariable("order_id") Long order_id
    ){
        try {

            orderService.deleteOrder(order_id);

            return ResponseEntity.ok("Order is deleted successfully");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
