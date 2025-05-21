package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.CartItemRequest;
import com.s2tv.sportshop.dto.response.CartItemResponse;
import com.s2tv.sportshop.dto.response.CartResponse;
import com.s2tv.sportshop.model.Cart;
import com.s2tv.sportshop.model.CartItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // Convert từ CartItemRequest thành CartItem entity
    CartItem toCartItem(CartItemRequest request);

    // Convert từ Cart entity sang CartResponse DTO
    @Mapping(source = "products", target = "products")
    CartResponse toCartResponse(Cart cart);

    // Convert từ CartItem entity sang CartItemResponse DTO
    CartItemResponse toCartItemResponse(CartItem cartItem);

    // Convert list
    List<CartItemResponse> toCartItemResponseList(List<CartItem> items);
}
