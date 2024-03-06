package com.ecommerce.shopapp.controllers;


import com.ecommerce.shopapp.dtos.UserDto;
import com.ecommerce.shopapp.dtos.UserLoginDto;
import com.ecommerce.shopapp.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService iUserService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDto userDto,
            BindingResult result){
        try {
            if (result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            if (!userDto.getPassword().equals(userDto.getRetypePassword())){
                return ResponseEntity.badRequest().body("The password not match");
            }

            iUserService.createUser(userDto);
            return ResponseEntity.ok("The user is created");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto){

        String token = iUserService.login(userLoginDto.getPhoneNumber(), userLoginDto.getPassword());

        return ResponseEntity.ok("Login successful");
    }
}
