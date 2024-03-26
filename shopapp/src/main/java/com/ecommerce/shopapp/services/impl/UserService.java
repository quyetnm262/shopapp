package com.ecommerce.shopapp.services.impl;

import com.ecommerce.shopapp.dtos.UpdateUserDto;
import com.ecommerce.shopapp.dtos.UserDto;
import com.ecommerce.shopapp.exceptions.DataNotFoundException;
import com.ecommerce.shopapp.exceptions.PermissionDenyException;
import com.ecommerce.shopapp.models.Role;
import com.ecommerce.shopapp.models.User;
import com.ecommerce.shopapp.repositories.RoleRepository;
import com.ecommerce.shopapp.repositories.UserRepository;
import com.ecommerce.shopapp.services.IUserService;
import com.ecommerce.shopapp.components.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public String login(String phoneNumber,
                        String password,
                        Long roleId) throws Exception {

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

    @Override
    public User getUserDetailsFromToken(String token) throws Exception{

        if (jwtTokenUtils.isTokenExpired(token)){
            throw new Exception("Token is expired");
        }

        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()){
            return user.get();
        }else {
            throw new Exception("User not found");
        }

    }

    @Transactional
    @Override
    public User updateUser(UpdateUserDto userDto, Long userId) throws Exception{

        User existedUser = userRepository
                .findById(userId)
                .orElseThrow(
                        ()-> new DataNotFoundException("User not found")
                );

        String newPhoneNumber = userDto.getPhoneNumber();
        if (!existedUser.getPhoneNumber().equals(newPhoneNumber)
        && userRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new DataIntegrityViolationException("Phone Number already existed");
        }

        if (userDto.getFullName() != null){
            existedUser.setFullName(userDto.getFullName());
        }

        if (userDto.getDateOfBirth() != null){
            existedUser.setDateOfBirth(userDto.getDateOfBirth());
        }

        if (userDto.getAddress() != null){
            existedUser.setAddress(userDto.getAddress());
        }

        if (userDto.getGoogleAccountId() > 0){
            existedUser.setGoogleAccountId(userDto.getGoogleAccountId());
        }

        if (userDto.getFacebookAccountId() > 0){
            existedUser.setFacebookAccountId(userDto.getFacebookAccountId());
        }

        if (userDto.getPassword() != null
        && !userDto.getPassword().isEmpty()){

            if (!userDto.getRetypePassword().equals(userDto.getPassword())){

                throw new DataNotFoundException("Password and retype password not match together");
            }

            String newPassword = userDto.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existedUser.setPassword(encodedPassword);
        }



        return userRepository.save(existedUser);
    }
}
