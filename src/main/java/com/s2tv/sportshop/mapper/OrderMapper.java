package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.enums.DiscountType;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Autowired
    protected DiscountRepository discountRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProductRepository productRepository;

    // Map từ OrderRequest -> Order (tạo mới)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(source = "discount_ids", target = "discount_ids", qualifiedByName = "mapDiscountIds"),
            @Mapping(source = "user_id", target = "user_id", qualifiedByName = "mapUserId"),
            @Mapping(source = "products", target = "products", qualifiedByName = "mapProducts")  // ✅ map products luôn
    })
    public abstract Order toOrder(OrderRequest request);

    // Map update từ request -> Order (update tồn tại)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(source = "discount_ids", target = "discount_ids", qualifiedByName = "mapDiscountIds"),
            @Mapping(source = "user_id", target = "user_id", qualifiedByName = "mapUserId"),
    })
    public abstract void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);

    // Map từ Order -> Response
    public abstract OrderResponse toOrderResponse(Order order);

    // ===== Map helpers =====

    @Named("mapDiscountIds")
    protected List<Discount> mapDiscountIds(List<String> discountIds) {
        if (discountIds == null || discountIds.isEmpty()) {
            return List.of();
        }

        List<Discount> discounts = discountRepository.findAllById(discountIds);

        if (discounts.size() != discountIds.size()) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_FOUND);
        }

        long shippingCount = discounts.stream()
                .filter(d -> d.getDiscountType() == DiscountType.SHIPPING)
                .count();

        long productCount = discounts.stream()
                .filter(d -> d.getDiscountType() == DiscountType.PRODUCT)
                .count();

        if (shippingCount > 1 || productCount > 1) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_COMBINATION);
        }

        return discounts;
    }

    @Named("mapUserId")
    protected User mapUserId(String userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));
    }

    @Named("mapProducts")
    protected List<OrderProduct> mapProducts(List<OrderProduct> productRequests) {
        if (productRequests == null || productRequests.isEmpty()) {
            return List.of();
        }

        // Lấy danh sách productId từ request
        List<String> productIds = productRequests.stream()
                .map(OrderProduct::getProduct_id)
                .toList();

        // Lấy danh sách Product từ DB (ProductRepository)
        List<Product> products = productRepository.findAllById(productIds);

        // Đưa về Map để tra cứu nhanh
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Map từng OrderProduct request sang OrderProduct đầy đủ info
        return productRequests.stream()
                .map(p -> {
                    Product product = productMap.get(p.getProduct_id());
                    if (product == null) {
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    }

                    return OrderProduct.builder()
                            .product_id(product.getId())
                            .product_title(product.getProduct_title())   // Tự động thêm title từ DB
                            .quantity(p.getQuantity() == 0 ? 1 : p.getQuantity())  // Nếu chưa nhập -> mặc định 1
                            .size(p.getSize())   // Có thể null nếu chưa nhập
                            .price(product.getProduct_price())   // Tự động thêm price từ DB
                            .build();
                })
                .collect(Collectors.toList());
    }

}
