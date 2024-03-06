package com.ecommerce.shopapp.dtos;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
//    private int id;

    @NotEmpty(message = "Category name not empty")
    private String name;
}
