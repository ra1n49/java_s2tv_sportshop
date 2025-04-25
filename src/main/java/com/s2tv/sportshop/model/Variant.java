package com.s2tv.sportshop.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "Variant")
public class Variant {
    @Id
    private String id;

    private String variant_size;
    private double variant_price = 0.0;
    private int variant_countInStock = 0;
}
