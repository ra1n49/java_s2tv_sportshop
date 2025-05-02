package com.s2tv.sportshop.model;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchHistory {
    private String message;
    private String filters;
    private Date searchedAt = new Date();
}