package com.s2tv.sportshop.model;

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
    @AllArgsConstructor
    public static class Message {
        String role;
        String content;
    }
}
