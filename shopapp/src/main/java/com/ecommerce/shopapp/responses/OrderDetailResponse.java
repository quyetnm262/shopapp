package com.ecommerce.shopapp.responses;

import com.ecommerce.shopapp.models.Order;
import com.ecommerce.shopapp.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    @JsonProperty("order_id")
    private Order order;

    private Product product;

    private Float price;

    @JsonProperty("number_of_products")
    private Integer numberOfProducts;

    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;

}
