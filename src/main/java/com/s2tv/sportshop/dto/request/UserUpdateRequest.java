package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.Gender;
import com.s2tv.sportshop.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String fullName;
    private MultipartFile avatarimg;
    private String email;
    private String phone;
    private List<Address> addresses;
    private String birth;
    private Gender gender;
}
