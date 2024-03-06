package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.UserDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.models.Role;
import com.ecommerce.shopapp.models.User;
import com.ecommerce.shopapp.repositories.RoleRepository;
import com.ecommerce.shopapp.repositories.UserRepository;
import com.ecommerce.shopapp.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    @Override
    public User createUser(UserDto userDto) throws DataNotFoundException {
        String phoneNumber = userDto.getPhoneNumber();
        //Kiểm tra xem số điện thoai đã tồn tại chưa
        boolean checkExist = userRepository.existsByPhoneNumber(phoneNumber);
        if (checkExist){
            throw new DataIntegrityViolationException("Phone number already exist");
        }
        //Convert Dto to Model
        User newUser = User.builder()
                .fullName(userDto.getFullName())
                .phoneNumber(userDto.getPhoneNumber())
                .password(userDto.getPassword())
                .address(userDto.getAddress())
                .dateOfBirth(userDto.getDateOfBirth())
                .facebookAccountId(userDto.getFacebookAccountId())
                .googleAccountId(userDto.getGoogleAccountId())
                .build();
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(()-> new DataNotFoundException("Role not found"));

        newUser.setRoleId(role);
        newUser.setActive(true);

        //Kiểm tra néu có accountId, không yêu cầu password
        if (userDto.getGoogleAccountId() == 0 && userDto.getFacebookAccountId() ==0){
            String password = userDto.getPassword();
//          Mã hoá mật khẩu
//            String encodedPassword =


        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) {

        // Lien quan toi Security

        return null;
    }
}
