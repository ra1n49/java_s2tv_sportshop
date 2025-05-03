package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderStatus", constant = "PENDING")
    @Mapping(target = "orderDate", expression = "java(new java.util.Date())")
    @Mapping(target = "isFeedback", constant = "false")
    Order toOrder(OrderRequest request);

    OrderResponse toOrderResponse(Order order);
}
