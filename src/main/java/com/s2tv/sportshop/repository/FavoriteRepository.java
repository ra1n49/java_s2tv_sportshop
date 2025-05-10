package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    Optional<Favorite> findByUserId(String userId);
}
