package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.CartItemRequest;
import com.s2tv.sportshop.dto.response.CartResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.CartMapper;
import com.s2tv.sportshop.model.Cart;
import com.s2tv.sportshop.model.CartItem;
import com.s2tv.sportshop.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    CartMapper cartMapper;

    public CartResponse createCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .cartItems(new ArrayList<>())
                                .build()));
        return cartMapper.toCartResponse(cart);
    }

    public CartResponse addItemToCart(String userId, CartItemRequest cartItemRequest) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .cartItems(new ArrayList<>())
                                .build()));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(cartItemRequest.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + cartItemRequest.getQuantity());
        } else {
            CartItem newItem = cartMapper.toCartItem(cartItemRequest);
            cart.getCartItems().add(newItem);
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse updateItemQuantity(String userId, String productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        item.setQuantity(quantity);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));
        return cartMapper.toCartResponse(cart);
    }

    public void deleteCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
