package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.FavoriteUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.FavoriteUpdateResponse;
import com.s2tv.sportshop.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PatchMapping
    public ApiResponse<FavoriteUpdateResponse> updateFavourite(@RequestBody FavoriteUpdateRequest request,
                                                               HttpServletRequest httpRequest) {
//        String userId = (String) httpRequest.getAttribute("userId");
        String userId = "67de5e6bb06fa40016dab238";
        return ApiResponse.<FavoriteUpdateResponse>builder()
                .EC(0)
                .EM("Cập nhật danh sách sản phẩm yêu thích thành công")
                .result(favoriteService.updateFavourite(userId, request.getProduct()))
                .build();
    }

    @GetMapping
    public ApiResponse<List<String>> getFavourite(HttpServletRequest request) {
//        String userId = (String) request.getAttribute("userId");
        String userId = "67de5e6bb06fa40016dab238";

        return ApiResponse.<List<String>>builder()
                .EC(0)
                .EM("Lấy danh sách sản phẩm yêu thích thành công")
                .result(favoriteService.getFavourite(userId))
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> clearFavourites(HttpServletRequest request) {
//        String userId = (String) request.getAttribute("userId");
        String userId = "67de5e6bb06fa40016dab238";
        favoriteService.clearFavourites(userId);

        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa danh sách yêu thích thành công")
                .build();
    }
}
