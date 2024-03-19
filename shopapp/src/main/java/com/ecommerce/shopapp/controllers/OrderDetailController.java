package com.ecommerce.shopapp.controllers;

import com.ecommerce.shopapp.dtos.OrderDetailDto;
import com.ecommerce.shopapp.dtos.OrderDto;
import com.ecommerce.shopapp.responses.OrderDetailResponse;
import com.ecommerce.shopapp.services.IOrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDto orderDetailDto,
            BindingResult result){
        try {

            if (result.hasErrors()){
                List<String> errorContent = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorContent);
            }

            OrderDetailResponse orderDetailResponse = orderDetailService.createOrderDetail(orderDetailDto);

            return ResponseEntity.ok(orderDetailResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{order_detail_id}")
    public ResponseEntity<?> getOrderDetail(
        @Valid @PathVariable("order_detail_id") Long orderDetailId
    ){
        try {

            OrderDetailResponse orderDetailResponse = orderDetailService.getOrderDetail(orderDetailId);

            return ResponseEntity.ok(orderDetailResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<?> getOrderDetailsByOrderId(
            @Valid @PathVariable("order_id") Long orderId
    ){
        try {

            List<OrderDetailResponse> orderDetailResponses = orderDetailService.getOrderDetails(orderId);

            return ResponseEntity.ok(orderDetailResponses);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional

    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") Long orderDetailId,
            @RequestBody OrderDetailDto orderDetailDto,
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

            OrderDetailResponse orderDetailResponse = orderDetailService.updateOrderDetail(orderDetailId,orderDetailDto);

            return ResponseEntity.ok(orderDetailResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable("id") Long id
    ){
        try {
            orderDetailService.deleteOrderDetail(id);
            return ResponseEntity.ok("Delete the order detail have id = "+id+" successfully");

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
