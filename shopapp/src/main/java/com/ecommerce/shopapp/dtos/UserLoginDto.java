package com.ecommerce.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDto {

    @NotBlank(message = "phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "password is required")
    private String password;
}
