package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query(value = "{ '_id': { '$in': ?0 } }", count = true)
    long countProductsByIdIn(List<String> ids);
}
