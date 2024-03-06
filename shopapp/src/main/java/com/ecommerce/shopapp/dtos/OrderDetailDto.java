package com.ecommerce.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {


    @Min(value = 1, message = "The Order is must is > 0")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value = 1, message = "The Order is must is > 0")
    @JsonProperty("product_id")
    private Long productId;

    private Float price;

    @Min(value = 1, message = "The Order is must is > 0")
    @JsonProperty("number_of_products")
    private Integer numberOfProducts;

    @Min(value = 0, message = "Total money must be >= 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;


}
