package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.Discount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiscountRepository extends MongoRepository<Discount, String> {
    boolean existsByDiscountCode(String discountCode);
}
