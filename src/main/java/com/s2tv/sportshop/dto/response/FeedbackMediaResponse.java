package com.s2tv.sportshop.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackMediaResponse {
    private List<String> images;
    private List<String> videos;
}
