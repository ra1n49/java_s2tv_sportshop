package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.DiscountCreateRequest;
import com.s2tv.sportshop.dto.request.DiscountUpdateRequest;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.enums.DiscountStatus;
import com.s2tv.sportshop.enums.NotifyType;
import com.s2tv.sportshop.enums.Role;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.DiscountMapper;
import com.s2tv.sportshop.model.Discount;
import com.s2tv.sportshop.model.Notification;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.NotificationRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NotificationRepository notificationRepository;


    public DiscountResponse createDiscount(DiscountCreateRequest discountCreateRequest) {
        if (discountRepository.existsByDiscountCode(discountCreateRequest.getDiscountCode()))
            throw new AppException(ErrorCode.DISCOUNT_CODE_EXISTED);

        Discount newDiscount = discountMapper.toDiscount(discountCreateRequest);
        Discount savedDiscount = discountRepository.save(newDiscount);
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getRole() == Role.ADMIN) continue;

            List<String> discounts = user.getDiscounts();
            if (discounts == null) {
                discounts = new ArrayList<>();
            }
            discounts.add(savedDiscount.getId());
            user.setDiscounts(discounts);
        }
        userRepository.saveAll(allUsers);

        Date now = new Date();
        String startDate = new SimpleDateFormat("dd/MM/yyyy").format(savedDiscount.getDiscountStartDay());
        String endDate = new SimpleDateFormat("dd/MM/yyyy").format(savedDiscount.getDiscountEndDay());

        List<Notification> notifications = allUsers.stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .map(user -> Notification.builder()
                        .notifyType(NotifyType.KHUYEN_MAI)
                        .notifyTitle("Ưu đãi mới: " + savedDiscount.getDiscountTitle())
                        .notifyDescription(String.format("Từ %s đến %s, sử dụng mã \"%s\" để nhận giảm giá %d%%!",
                                startDate,
                                endDate,
                                savedDiscount.getDiscountCode(),
                                savedDiscount.getDiscountNumber()))
                        .discountId(savedDiscount.getId())
                        .imageUrl("https://cdn.lawnet.vn/uploads/tintuc/2022/11/07/khuyen-mai.jpg")
                        .userId(user.getId())
                        .read(false)
                        .createdAt(now)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);

        return discountMapper.toDiscountResponse(savedDiscount);
    }

    public DiscountResponse getDetailDiscount(String id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NON_EXISTED));

        return discountMapper.toDiscountResponse(discountRepository.save(discount));
    }

    public List<DiscountResponse> getAllDiscount() {
        List<Discount> discounts = discountRepository.findAll();
        return discounts.stream()
                .map(discountMapper::toDiscountResponse)
                .toList();
    }

    public DiscountResponse updateDiscount(DiscountUpdateRequest request, String id){
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NON_EXISTED));

        discountMapper.updateDiscount(discount, request);
        return discountMapper.toDiscountResponse(discountRepository.save(discount));
    }

    public void deleteDiscount(String id){
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NON_EXISTED));

        discountRepository.delete(discount);
    }

    public List<DiscountResponse> getDiscountForOrder(String userId, List<String> productIds) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Date now = new Date();
        List<Discount> discounts = discountRepository.findByIdInAndStatusAndDiscountStartDayLessThanEqualAndDiscountEndDayGreaterThanEqual(
                user.getDiscounts(), DiscountStatus.ACTIVE, now, now
        );

        if (discounts.isEmpty()) {
            return Collections.emptyList();
        }
        List<Discount> applicableDiscounts = discounts.stream()
                .filter(discount -> {
                    if (discount.getDiscountAmount() <= 0) {
                        return false;
                    }

                    boolean appliesToProducts = products.stream().allMatch(product ->
                            product.getId() != null && discount.getApplicableProducts().stream()
                                    .anyMatch(dpid -> dpid.equals(product.getId()))
                    );

                    boolean appliesToCategories = products.stream().allMatch(product ->
                            product.getProductCategory() != null &&
                                    discount.getApplicableCategories().stream()
                                            .anyMatch(dcid -> dcid.equals(product.getProductCategory()))
                    );

                    return appliesToProducts || appliesToCategories;
                })
                .toList();
        return applicableDiscounts.stream()
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toList());
    }
}
