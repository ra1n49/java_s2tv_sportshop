package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.model.Order;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Map khi tạo Order mới từ request
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Order toOrder(OrderRequest request);

    // Map từ Order → Response
    OrderResponse toOrderResponse(Order order);

    // Map update Order từ request (bỏ qua id, createdAt)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);
}
