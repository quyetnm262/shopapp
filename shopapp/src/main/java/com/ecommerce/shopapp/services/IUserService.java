package com.ecommerce.shopapp.services;

import com.ecommerce.shopapp.dtos.UserDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.models.User;

public interface IUserService {
    User createUser(UserDto userDto) throws DataNotFoundException;

    String login(String phoneNumber, String password);
}
