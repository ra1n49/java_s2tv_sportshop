package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.DiscountCreateRequest;
import com.s2tv.sportshop.dto.request.DiscountUpdateRequest;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.model.Discount;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    Discount toDiscount(DiscountCreateRequest discountCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDiscount(@MappingTarget Discount discount, DiscountUpdateRequest discountUpdateRequest);

    DiscountResponse toDiscountResponse(Discount discount);
}
