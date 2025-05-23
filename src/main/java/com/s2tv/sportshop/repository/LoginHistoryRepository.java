package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.LoginHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoginHistoryRepository extends MongoRepository<LoginHistory, String> {
    List<LoginHistory> findAllByOrderByCreatedAtDesc();
}
