package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "Color")
public class Color {
    String color_name;
    Img imgs;
    List<Variant> variants;

    @Data
    public static class Img {
        private String img_main;
        private List<String> img_subs;
    }
}