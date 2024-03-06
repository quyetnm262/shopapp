package com.ecommerce.shopapp.dtos;

import com.ecommerce.shopapp.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDto {

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's id must be > 0")
    private Long productId;

    @Size(min = 5, max = 200, message = "Name must be between 5 and 200")
    @JsonProperty("image_url")
    private String imageUrl;
}
