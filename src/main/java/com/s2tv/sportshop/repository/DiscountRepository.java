package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.enums.DiscountStatus;
import com.s2tv.sportshop.model.Discount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface DiscountRepository extends MongoRepository<Discount, String> {
    boolean existsByDiscountCode(String discountCode);
    List<Discount> findByIdInAndStatusAndDiscountStartDayLessThanEqualAndDiscountEndDayGreaterThanEqual(
            List<String> ids,
            DiscountStatus status,
            Date startDay,
            Date endDay
    );
}
