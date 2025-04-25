package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {
    Optional<Users> findById(String id);
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
