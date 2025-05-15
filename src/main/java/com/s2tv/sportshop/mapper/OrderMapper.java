package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderCode", ignore = true)
    @Mapping(target = "orderStatus", constant = "CHO_XAC_NHAN")
    @Mapping(target = "orderTotalPrice", ignore = true)
    @Mapping(target = "orderTotalFinal", ignore = true)
    @Mapping(target = "orderTotalDiscount", ignore = true)
    @Mapping(target = "checkoutUrl", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "discountIds", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "isPaid", constant = "false")
    @Mapping(target = "isFeedback", constant = "false")
    @Mapping(target = "estimatedDeliveryDate", ignore = true)
    @Mapping(target = "receivedDate", ignore = true)
    Order toOrder(OrderRequest request);

    OrderResponse toOrderResponse(Order order);
}
