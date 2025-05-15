package com.s2tv.sportshop.repository;

import org.springframework.data.mongodb.repository.MongoRepository;  // ✅ Đúng (Dành cho MongoDB)

import org.springframework.stereotype.Repository;

@Repository
public interface ShippingAddressRepository extends MongoRepository<ShippingAddress, String> {

    /**
     * Kiểm tra địa chỉ giao hàng có tồn tại theo id không
     * (Spring Data JPA tự tạo SQL: SELECT count(*) FROM shipping_address WHERE id = ?)
     */
    boolean existsById(String id);
}
