package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.StoreCreateRequest;
import com.s2tv.sportshop.dto.request.StoreUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.model.Store;
import com.s2tv.sportshop.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StoreController {
    StoreService storeService;

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<Store> createStore(
            @ModelAttribute StoreCreateRequest storeData,
            HttpServletRequest request) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        return ApiResponse.<Store>builder()
                .EC(0)
                .EM("Tạo thông tin của hàng thành công")
                .result(storeService.createStore(storeData, multipartRequest))
                .build();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ApiResponse<Store> updateStore(
            @PathVariable("id") String storeId,
            @ModelAttribute StoreUpdateRequest request,
            MultipartHttpServletRequest multipartRequest) throws IOException {

        return ApiResponse.<Store>builder()
                .EC(0)
                .EM("Cập nhật thông tin cửa hàng thành công")
                .result(storeService.updateStore(storeId, request, multipartRequest))
                .build();
    }

    @GetMapping("/get-detail/{id}")
    public ApiResponse<Store> getDetailStore(@PathVariable("id") String storeId) {
        return ApiResponse.<Store>builder()
                .EC(0)
                .EM("Lấy thông tin cửa hàng thành công")
                .result(storeService.getDetailStore(storeId))
                .build();
    }
}
