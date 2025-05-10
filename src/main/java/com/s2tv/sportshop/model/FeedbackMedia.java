package com.s2tv.sportshop.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackMedia {
    private List<String> images;
    private List<String> videos;
}