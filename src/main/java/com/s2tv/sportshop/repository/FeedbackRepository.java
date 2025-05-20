package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByProductId(String productId);
    List<Feedback>findByProductIdAndDeletedFalse(String productId);
    boolean existsByOrderIdAndProductIdAndUserIdAndColorAndVariant(
            String orderId, String productId, String userId, String color, String variant
    );
}
