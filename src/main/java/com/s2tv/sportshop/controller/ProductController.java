package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.service.CloudinaryService;
import com.s2tv.sportshop.service.ProductService;
import com.s2tv.sportshop.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") Product product,  // Nhận trực tiếp đối tượng Product
            @RequestPart("file") MultipartFile file   // Nhận ảnh
    ) {
        try {
            // Upload file lên Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "products", "image");
            product.setProduct_img(imageUrl);

            // Tạo sản phẩm
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }

}
