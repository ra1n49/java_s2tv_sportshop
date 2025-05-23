package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.LoginActivityRequest;
import com.s2tv.sportshop.dto.response.LoginActivityResponse;
import com.s2tv.sportshop.dto.response.LoginHistoryResponse;
import com.s2tv.sportshop.model.LoginActivity;
import com.s2tv.sportshop.model.LoginHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoginHistoryMapper {
    LoginHistoryResponse toLoginHistoryResponse (LoginHistory loginHistory);
    LoginActivityResponse toLoginActivityResponse (LoginActivity activity);

    @Mapping(target = "action", source = "action")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "prevStatus", source = "prevStatus")
    @Mapping(target = "newStatus", source = "newStatus")
    LoginActivity toLoginActivity (LoginActivityRequest loginActivityRequest);
    List<LoginActivityResponse> toLoginActivitiesResponse (List<LoginActivity> loginActivities);
    List<LoginHistoryResponse> toLoginHistoriesResponse (List<LoginHistory> loginHistories);
}
