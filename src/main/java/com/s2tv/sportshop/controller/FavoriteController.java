package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.FavoriteUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.FavoriteUpdateResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PreAuthorize("isAuthenticated()")
    @PatchMapping
    public ApiResponse<FavoriteUpdateResponse> updateFavourite(@RequestBody FavoriteUpdateRequest request,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<FavoriteUpdateResponse>builder()
                .EC(0)
                .EM("Cập nhật danh sách sản phẩm yêu thích thành công")
                .result(favoriteService.updateFavourite(userId, request.getProduct()))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<List<String>> getFavourite(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();

        return ApiResponse.<List<String>>builder()
                .EC(0)
                .EM("Lấy danh sách sản phẩm yêu thích thành công")
                .result(favoriteService.getFavourite(userId))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ApiResponse<Void> clearFavourites(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();

        favoriteService.clearFavourites(userId);

        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa danh sách yêu thích thành công")
                .build();
    }
}
