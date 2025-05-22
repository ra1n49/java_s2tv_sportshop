package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.CartItemRequest;
import com.s2tv.sportshop.dto.response.CartItemResponse;
import com.s2tv.sportshop.dto.response.CartResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.CartMapper;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.CartRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;

    public CartResponse createCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .products(new ArrayList<>())
                                .build()));
        return cartMapper.toCartResponse(cart);
    }

    public CartResponse addItemToCart(String userId, CartItemRequest cartItemRequest) {
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Color matchedColor = product.getColors().stream()
                .filter(c -> c.getColorName().equals(cartItemRequest.getColorName()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

        Variant matchedVariant = matchedColor.getVariants().stream()
                .filter(v -> v.getVariantSize().equals(cartItemRequest.getVariantName()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .products(new ArrayList<>())
                                .build()));

        List<CartItem> productList = cart.getProducts();
        if (productList == null) {
            productList = new ArrayList<>();
            cart.setProducts(productList);
        }

        Optional<CartItem> existingItem = cart.getProducts().stream()
                .filter(item ->
                        item.getProductId().equals(cartItemRequest.getProductId()) &&
                                item.getColorName().equals(cartItemRequest.getColorName()) &&
                                item.getVariantName().equals(cartItemRequest.getVariantName())
                )
                .findFirst();

        int currentQty = existingItem.map(CartItem::getQuantity).orElse(0);
        int requestedQty = cartItemRequest.getQuantity();
        int maxStock = matchedVariant.getVariantCountInStock();

        if (currentQty + requestedQty > maxStock) {
            throw new AppException(ErrorCode.STOCK_LIMIT_EXCEEDED);
        }

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(currentQty + requestedQty);
        } else {
            CartItem newItem = cartMapper.toCartItem(cartItemRequest);
            cart.getProducts().add(newItem);
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse removeItemFromCart(String userId, String productId, String colorName, String variantName) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Color matchedColor = product.getColors().stream()
                .filter(c -> c.getColorName().equals(colorName))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

        Variant matchedVariant = matchedColor.getVariants().stream()
                .filter(v -> v.getVariantSize().equals(variantName))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        cart.getProducts().removeIf(item ->
                item.getProductId().equals(productId) &&
                        item.getColorName().equals(colorName) &&
                        item.getVariantName().equals(variantName)
        );

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse updateItemQuantity(String userId, String productId, String colorName, String variantName) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Color matchedColor = product.getColors().stream()
                .filter(c -> c.getColorName().equals(colorName))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

        Variant matchedVariant = matchedColor.getVariants().stream()
                .filter(v -> v.getVariantSize().equals(variantName))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        List<CartItem> products = cart.getProducts();
        Optional<CartItem> itemOptional = products.stream()
                .filter(ci ->
                        ci.getProductId().equals(productId) &&
                                ci.getColorName().equals(colorName) &&
                                ci.getVariantName().equals(variantName))
                .findFirst();

        if (itemOptional.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        CartItem item = itemOptional.get();
        if (item.getQuantity() <= 1) {
            throw new AppException(ErrorCode.MIN_QUANTITY_REACHED);
        }

        item.setQuantity(item.getQuantity() - 1);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            return CartResponse.builder()
                    .userId(userId)
                    .products(List.of())
                    .build();
        }

        List<CartItem> items = cart.getProducts();
        AtomicBoolean updated = new AtomicBoolean(false);

        List<CartItemResponse> cartItemResponses = items.stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Color matchedColor = product.getColors().stream()
                    .filter(c -> c.getColorName().equals(item.getColorName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

            Variant matchedVariant = matchedColor.getVariants().stream()
                    .filter(v -> v.getVariantSize().equals(item.getVariantName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

            int stock = matchedVariant.getVariantCountInStock();
            if (item.getQuantity() > stock) {
                item.setQuantity(stock);
                updated.set(true);
            }

            return CartItemResponse.builder()
                    .productId(item.getProductId())
                    .colorName(item.getColorName())
                    .variantName(item.getVariantName())
                    .quantity(item.getQuantity())
                    .product(product)
                    .build();
        }).toList();

        if (updated.get()) {
            cart.setProducts(items);
            cartRepository.save(cart);
        }

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .products(cartItemResponses)
                .build();
    }

    public void deleteCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));
        cart.getProducts().clear();
        cartRepository.save(cart);
    }

    public void clearCartByUserId(String userId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("userId").is(userId)),
                Update.update("products", Collections.emptyList()),
                Cart.class
        );
    }
}
