package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.UserUpdateRequest;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.UserMapper;
import com.s2tv.sportshop.model.Address;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        userMapper.updateUser(user, userUpdateData);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public String updateAvatar(String userId, MultipartFile file){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));
        try {
            String imageUrl = cloudinaryService.uploadAvatar(file);
            user.setAvtimg(imageUrl);
            userRepository.save(user);
            return imageUrl;
        } catch (IOException e) {
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public UserResponse addAddress(String userId, Address addressData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        if (user.getAddresses() == null) {
            user.setAddresses(new ArrayList<>());
            addressData.setIsDefault(true);
        } else if (user.getAddresses().isEmpty()) {
            addressData.setIsDefault(true);
        }

        if (addressData.getIsDefault()) {
            user.getAddresses().forEach(address -> address.setIsDefault(false));
        }

        user.getAddresses().add(addressData);
        return userMapper.toUserResponse(userRepository.save(user));
    }


    public Address updateAddress(String userId, int index, Address updateData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        List<Address> addresses = user.getAddresses();
        if (index < 0 || index >= addresses.size()) {
            throw new AppException(ErrorCode.INVALID_INDEX);
        }

        Address address = addresses.get(index);

        if (updateData.getName() != null) address.setName(updateData.getName());
        if (updateData.getPhone() != null) address.setPhone(updateData.getPhone());
        if (updateData.getHomeAddress() != null) address.setHomeAddress(updateData.getHomeAddress());
        if (updateData.getProvince() != null) address.setProvince(updateData.getProvince());
        if (updateData.getDistrict() != null) address.setDistrict(updateData.getDistrict());
        if (updateData.getWard() != null) address.setWard(updateData.getWard());
        if (updateData.getIsDefault() != null) address.setIsDefault(updateData.getIsDefault());


        if (Boolean.TRUE.equals(updateData.getIsDefault())) {
            for (int i = 0; i < addresses.size(); i++) {
                if (i != index) addresses.get(i).setIsDefault(false);
            }
        }

        user.setAddresses(addresses);
        userRepository.save(user);
        return address;
    }

    public void deleteAddress(String userId, int index) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));
        List<Address> addresses = user.getAddresses();

        if (index < 0 || index >= addresses.size()) {
            throw new AppException(ErrorCode.INVALID_INDEX);
        }

        boolean isDefaultRemove = addresses.get(index).getIsDefault();
        addresses.remove(index);

        if (isDefaultRemove && !addresses.isEmpty()) {
            addresses.getFirst().setIsDefault(true);
        }

        userRepository.save(user);
    }
}
