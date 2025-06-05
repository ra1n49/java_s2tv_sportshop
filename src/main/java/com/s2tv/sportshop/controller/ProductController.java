package com.s2tv.sportshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.request.ProductGetAllRequest;
import com.s2tv.sportshop.dto.request.ProductUpdateRequest;
import com.s2tv.sportshop.dto.response.*;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<ProductCreateResponse> createProduct(
            @ModelAttribute ProductCreateRequest productCreateRequest,
            @RequestParam(value = "colors", required = true) String colorsJson,
            HttpServletRequest request
    ) throws JsonProcessingException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return ApiResponse.<ProductCreateResponse>builder()
                .EC(0)
                .EM("Tạo sản phẩm mới thành công")
                .result(productService.createProduct(productCreateRequest, colorsJson, multipartRequest))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ApiResponse<ProductUpdateResponse> updateProduct(
            @PathVariable("id") String productId,
            @ModelAttribute ProductUpdateRequest productUpdateRequest,
            @RequestParam(value = "colors", required = false) String colorJson,
            HttpServletRequest request
    ) throws JsonProcessingException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        return ApiResponse.<ProductUpdateResponse>builder()
                .EC(0)
                .EM("Cập nhật sản phẩm thành công")
                .result(productService.updateProduct(productId, productUpdateRequest, colorJson, multipartRequest))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable("id") String productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa sản phẩm thành công")
                .build();
    }

    @GetMapping("/get-details/{id}")
    public ApiResponse<ProductGetDetailsResponse> getDetailsProduct(@PathVariable("id") String productId) {
        return ApiResponse.<ProductGetDetailsResponse>builder()
                .EC(0)
                .EM("Lấy chi tiết sản phẩm thành công")
                .result(productService.getDetailsProduct(productId))
                .build();
    }

    @GetMapping("get-all")
    public ApiResponse<ProductGetAllResponse> getAllProduct(@ModelAttribute ProductGetAllRequest request) {
        return ApiResponse.<ProductGetAllResponse>builder()
                .EC(0)
                .EM("Lấy danh sách sản phẩm thành công")
                .result(productService.getAllProduct(request))
                .build();
    }

    @GetMapping("/recommend/{userId}")
    public ApiResponse<List<ProductGetDetailsResponse>> getRecommendedProducts(@PathVariable String userId) {
        List<ProductGetDetailsResponse> recommendedProducts = productService.getRecommendedProducts(userId);

        return ApiResponse.<List<ProductGetDetailsResponse>>builder()
                .EC(0)
                .EM("Lấy sản phẩm gợi ý thành công")
                .result(recommendedProducts)
                .build();
    }
}