package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.DiscountCreateRequest;
import com.s2tv.sportshop.dto.request.DiscountUpdateRequest;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.enums.Role;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.DiscountMapper;
import com.s2tv.sportshop.model.Discount;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountMapper discountMapper;

    @Autowired
    private UserRepository userRepository;


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
}
