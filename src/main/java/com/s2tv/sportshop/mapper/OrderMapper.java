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
            @Mapping(target = "order_status", constant = "PENDING"),
            @Mapping(target = "order_date", expression = "java(new java.util.Date())"),
            @Mapping(target = "order_delivery_date", ignore = true), // chưa có ngày giao hàng thực tế
            @Mapping(target = "order_total_price", ignore = true),
            @Mapping(target = "order_final_price", ignore = true),
            @Mapping(target = "order_total_discount", ignore = true),
            @Mapping(target = "order_note", source = "order_note"),
            @Mapping(target = "is_feedback", constant = "false"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Order toOrder(OrderRequest request);

    // Map từ Order sang OrderResponse (trả dữ liệu cho client)
    OrderResponse toOrderResponse(Order order);

    // Cập nhật Order từ OrderRequest (khi update đơn hàng)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "order_status", ignore = true),
            @Mapping(target = "order_date", ignore = true),
            @Mapping(target = "order_delivery_date", ignore = true),
            @Mapping(target = "order_total_price", ignore = true),
            @Mapping(target = "order_final_price", ignore = true),
            @Mapping(target = "order_total_discount", ignore = true),
            @Mapping(target = "order_note", source = "order_note"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);
}
