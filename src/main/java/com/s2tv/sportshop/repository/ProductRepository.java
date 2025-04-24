package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
