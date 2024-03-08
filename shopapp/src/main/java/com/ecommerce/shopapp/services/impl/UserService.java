package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.UserDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.exceptions.PermissionDenyException;
import com.ecommerce.shopapp.models.Role;
import com.ecommerce.shopapp.models.User;
import com.ecommerce.shopapp.repositories.RoleRepository;
import com.ecommerce.shopapp.repositories.UserRepository;
import com.ecommerce.shopapp.services.IUserService;
import com.ecommerce.shopapp.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;


    @Override
    public User createUser(UserDto userDto) throws Exception {
        String phoneNumber = userDto.getPhoneNumber();
        //Kiểm tra xem số điện thoai đã tồn tại chưa
        boolean checkExist = userRepository.existsByPhoneNumber(phoneNumber);
        if (checkExist){
            throw new DataIntegrityViolationException("Phone number already exist");
        }
        Role role = roleRepository.findById(userDto.getRoleId())
        .orElseThrow(()-> new DataNotFoundException("Role not found"));

        if (role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("Cannot create ADMIN account");
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

        newUser.setRole(role);
        newUser.setActive(true);

        //Kiểm tra néu có accountId, không yêu cầu password
        if (userDto.getGoogleAccountId() == 0 && userDto.getFacebookAccountId() ==0){
            String password = userDto.getPassword();
//          Mã hoá mật khẩu
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);

        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {

        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);

        if (optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid phone number or password");
        }
        User existedUser = optionalUser.get();

        //Check password
        if (existedUser.getGoogleAccountId() == 0
                && existedUser.getFacebookAccountId() ==0){

            if (!passwordEncoder.matches(password, existedUser.getPassword())){
                throw new DataNotFoundException("Invalid phone number or password");
            }

        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,
                existedUser.getAuthorities()
        );
        //Authenticate with java spring security
        authenticationManager.authenticate(authenticationToken);

        //Tra ve token
        return jwtTokenUtils.generateToken(existedUser);
    }
}
