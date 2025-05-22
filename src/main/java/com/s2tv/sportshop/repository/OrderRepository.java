package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderCode(Long orderCode);
    List<Order> findByOrderStatus(OrderStatus statusEnum);
    List<Order> findByUserId(String userId);
    List<Order> findByUserIdAndOrderStatus(String userId, OrderStatus status);
}
