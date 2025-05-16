package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.model.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {
    Optional<ChatHistory> findByUserId(String userId);
}
