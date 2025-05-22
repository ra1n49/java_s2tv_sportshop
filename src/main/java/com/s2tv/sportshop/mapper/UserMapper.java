package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.UserCreateRequest;
import com.s2tv.sportshop.dto.request.UserUpdateRequest;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.model.User;
import org.mapstruct.*;

import java.text.SimpleDateFormat;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", constant = "USER")
    User toUser(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

    default UserResponse toUserResponse(User user){
        if (user == null) return null;

        String birthStr = null;
        if (user.getBirth() != null) {
            birthStr = new SimpleDateFormat("yyyy-MM-dd").format(user.getBirth());
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avtimg(user.getAvtimg())
                .email(user.getEmail())
                .phone(user.getPhone())
                .addresses(user.getAddresses())
                .birth(birthStr)
                .gender(user.getGender())
                .role(user.getRole())
                .searchhistory(user.getSearchhistory())
                .discounts(user.getDiscounts())
                .build();
    }
}
