package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.model.Discount;
import com.s2tv.sportshop.model.Order;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Autowired
    protected DiscountRepository discountRepository;

    @Autowired
    protected UserRepository userRepository;

    // Map khi tạo Order mới từ request
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(source = "discount_ids", target = "discount_ids", qualifiedByName = "mapDiscountIds"),
            @Mapping(source = "user_id", target = "user_id", qualifiedByName = "mapUserId")
            // Không cần mapping shipping_address vì cùng kiểu
    })
    public abstract Order toOrder(OrderRequest request);

    // Map từ Order → Response
    public abstract OrderResponse toOrderResponse(Order order);

    // Map update Order từ request (bỏ qua id, createdAt)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(source = "discount_ids", target = "discount_ids", qualifiedByName = "mapDiscountIds"),
            @Mapping(source = "user_id", target = "user_id", qualifiedByName = "mapUserId")
            // Không cần mapping shipping_address vì cùng kiểu
    })
    public abstract void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);

    @Named("mapDiscountIds")
    protected List<Discount> mapDiscountIds(List<String> discountIds) {
        if (discountIds == null || discountIds.isEmpty()) {
            return List.of();
        }
        return discountRepository.findAllById(discountIds);
    }

    @Named("mapUserId")
    protected User mapUserId(String userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy User với id: " + userId));
    }
}
