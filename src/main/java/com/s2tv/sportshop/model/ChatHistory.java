package com.s2tv.sportshop.model;

import com.s2tv.sportshop.dto.response.ProductShortResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "ChatHistory")
public class ChatHistory {
    @Id
    String id;
    String userId;
    List<Message> messages = new ArrayList<>();

    @LastModifiedDate
    Date updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        String role;
        String content;
        List<ProductShortResponse> suggestedProducts;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
            this.suggestedProducts = null;
        }
    }
}
