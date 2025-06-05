package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.UserUpdateRequest;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.enums.DiscountStatus;
import com.s2tv.sportshop.enums.Gender;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.DiscountMapper;
import com.s2tv.sportshop.mapper.UserMapper;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.ChatHistoryRepository;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final ChatHistoryRepository chatHistoryRepository;

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

    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateData) throws ParseException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        if (userUpdateData.getFullName() != null && !userUpdateData.getFullName().isBlank()) {
            user.setFullName(userUpdateData.getFullName());
        }

        if (userUpdateData.getEmail() != null && !userUpdateData.getEmail().isBlank()) {
            user.setEmail(userUpdateData.getEmail());
        }

        if (userUpdateData.getPhone() != null && !userUpdateData.getPhone().isBlank()) {
            user.setPhone(userUpdateData.getPhone());
        }

        if (userUpdateData.getGender() != null) {
            user.setGender(Gender.valueOf(userUpdateData.getGender().name()));
        }

        if (userUpdateData.getBirth() != null && !userUpdateData.getBirth().isBlank()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = sdf.parse(userUpdateData.getBirth());
            user.setBirth(birthDate);
        }

        MultipartFile avatarFile = userUpdateData.getAvatarimg();
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String url = cloudinaryService.uploadAvatar(avatarFile);
                user.setAvtimg(url);
            } catch (IOException e) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        return userMapper.toUserResponse(userRepository.save(user));
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

    public List<DiscountResponse> getDiscountUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        Date now = new Date();

        List<Discount> discounts = discountRepository.findByIdInAndStatusAndDiscountStartDayLessThanEqualAndDiscountEndDayGreaterThanEqual(
                user.getDiscounts(),
                DiscountStatus.ACTIVE,
                now,
                now
        );

        return discounts.stream()
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toList());
    }

    public List<ChatHistory.Message> getChatHistoryByUserId(String userId) {
        return chatHistoryRepository.findByUserId(userId)
                .map(ChatHistory::getMessages)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_FOUND));
    }

    public void deleteSearchHistory(String userId, int index) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        List<SearchHistory> searchHistory = user.getSearchhistory();

        if (searchHistory == null || index < 0 || index >= searchHistory.size()) {
            throw new IllegalArgumentException("Chỉ số không phù hợp.");
        }

        searchHistory.remove(index);
        user.setSearchhistory(searchHistory);
        userRepository.save(user);
    }
}
