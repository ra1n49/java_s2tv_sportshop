package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.Gender;
import com.s2tv.sportshop.enums.Role;
import com.s2tv.sportshop.model.Address;
import com.s2tv.sportshop.model.SearchHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String fullName;
    private String avtimg;
    private String email;
    private String phone;
    private List<Address> addresses;
    private String birth;
    private Gender gender;
    private Role role;
    private List<SearchHistory> searchhistory;
    private List<String> discounts;
}
