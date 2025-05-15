package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.DiscountCreateRequest;
import com.s2tv.sportshop.dto.request.DiscountUpdateRequest;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.enums.Role;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.DiscountMapper;
import com.s2tv.sportshop.model.Discount;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountMapper discountMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


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
            throw new AppException(ErrorCode.PRODUCT_NOTFOUND);
        }

        Date now = new Date();
        List<Discount> discounts = discountRepository.findByIdInAndStatusAndDiscountStartDayLessThanEqualAndDiscountEndDayGreaterThanEqual(
                user.getDiscounts(), "active", now, now
        );

        if (discounts.isEmpty()) {
            return Collections.emptyList();
        }
        List<Discount> applicableDiscounts = discounts.stream()
                .filter(discount -> {
                    boolean appliesToProducts = products.stream().allMatch(product ->
                            discount.getApplicableProducts().stream()
                                    .anyMatch(dpid -> dpid.equals(product.getId()))
                    );

                    boolean appliesToCategories = products.stream().allMatch(product ->
                            discount.getApplicableCategories().stream()
                                    .anyMatch(dcid -> dcid.equals(product.getProduct_category()))
                    );

                    return appliesToProducts || appliesToCategories;
                })
                .toList();
        return applicableDiscounts.stream()
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toList());
    }
}
