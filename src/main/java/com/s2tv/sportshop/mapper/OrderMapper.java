package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Map từ OrderRequest sang Order (khi tạo đơn hàng)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "orderStatus", constant = "PENDING"),
            @Mapping(target = "orderDate", expression = "java(new java.util.Date())"),
            @Mapping(target = "orderDeliveryDate", ignore = true), // chưa có ngày giao hàng thực tế
            @Mapping(target = "orderTotalPrice", ignore = true),
            @Mapping(target = "orderFinalPrice", ignore = true),
            @Mapping(target = "orderTotalDiscount", ignore = true),
            @Mapping(target = "orderNote", source = "orderNote"),
            @Mapping(target = "isFeedback", constant = "false"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Order toOrder(OrderRequest request);

    // Map từ Order sang OrderResponse (trả dữ liệu cho client)
    OrderResponse toOrderResponse(Order order);

    // Cập nhật Order từ OrderRequest (khi update đơn hàng)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "orderStatus", ignore = true),
            @Mapping(target = "orderDate", ignore = true),
            @Mapping(target = "orderDeliveryDate", ignore = true),
            @Mapping(target = "orderTotalPrice", ignore = true),
            @Mapping(target = "orderFinalPrice", ignore = true),
            @Mapping(target = "orderTotalDiscount", ignore = true),
            @Mapping(target = "orderNote", source = "orderNote"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);
}
