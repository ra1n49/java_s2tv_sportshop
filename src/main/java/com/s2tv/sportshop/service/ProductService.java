package com.s2tv.sportshop.service;

import com.s2tv.sportshop.model.Color;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.model.Variant;
import com.s2tv.sportshop.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product productData) {
        return productRepository.save(productData);
    }
}
