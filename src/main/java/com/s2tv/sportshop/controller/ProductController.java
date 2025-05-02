package com.s2tv.sportshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ApiResponse<ProductCreateResponse> createProduct(
            @ModelAttribute ProductCreateRequest productCreateRequest,
            @RequestParam(value = "colors", required = true) String colorsJson,
            HttpServletRequest request
    ) throws JsonProcessingException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//
//        // Lấy file ảnh chính sản phẩm
//        MultipartFile productMainImg = multipartRequest.getFile("product_img");
//        if (productMainImg == null || productMainImg.isEmpty()) {
//            return ApiResponse.<ProductCreateResponse>builder()
//                    .EC(1)
//                    .EM("Ảnh chính của sản phẩm là bắt buộc")
//                    .build();
//        }

        return productService.createProduct(productCreateRequest, colorsJson, multipartRequest);
    }
}