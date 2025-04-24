package com.s2tv.sportshop.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "Color")
public class Color {
    @Id
    private String id;

    private String color_name;
    private Img imgs;
    private List<Variant> variants;

    @Data
    public static class Img {
        private String img_main;
        private List<String> img_subs;
    }
}