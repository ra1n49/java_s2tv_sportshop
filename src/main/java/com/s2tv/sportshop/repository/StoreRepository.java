package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StoreRepository extends MongoRepository<Store, String> {
}
