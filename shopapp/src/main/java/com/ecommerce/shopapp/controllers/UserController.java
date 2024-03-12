package com.ecommerce.shopapp.controllers;


import com.ecommerce.shopapp.dtos.UserDto;
import com.ecommerce.shopapp.dtos.UserLoginDto;
import com.ecommerce.shopapp.models.User;
import com.ecommerce.shopapp.responses.LoginResponse;
import com.ecommerce.shopapp.responses.RegisterResponse;
import com.ecommerce.shopapp.services.IUserService;
import com.ecommerce.shopapp.components.LocalizationUtils;
import com.ecommerce.shopapp.utils.MessageKeys;
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

    private final LocalizationUtils localizationUtils;

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
                return ResponseEntity.badRequest().body(
                        localizationUtils.getLocalizationMessage(MessageKeys.PASSWORD_NOT_MATCH)
                );
            }

            User user = iUserService.createUser(userDto);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    RegisterResponse.builder()
                            .message(localizationUtils
                                    .getLocalizationMessage(MessageKeys.REGISTER_FAILED, e.getMessage()))
                            .build()
            );
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto){

        try {
            String token = iUserService.login(userLoginDto.getPhoneNumber(), userLoginDto.getPassword());

            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils
                            .getLocalizationMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils
                                    .getLocalizationMessage(MessageKeys.LOGIN_FAIL, e.getMessage()))
                            .build()
            );
        }
    }
}
