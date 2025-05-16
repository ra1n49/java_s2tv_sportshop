package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.CategoryCreateRequest;
import com.s2tv.sportshop.dto.request.CategoryUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.CategoryCreateResponse;
import com.s2tv.sportshop.dto.response.CategoryUpdateResponse;
import com.s2tv.sportshop.model.Category;
import com.s2tv.sportshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<CategoryCreateResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        return ApiResponse.<CategoryCreateResponse>builder()
                .EC(0)
                .EM("Tạo danh mục mới thành công")
                .result(categoryService.createCategory(request))
                .build();
    }

    @GetMapping("/get-detail/{id}")
    public ApiResponse<Category> getDetailCategory(@PathVariable("id") String categoryId) {
        return ApiResponse.<Category>builder()
                .EC(0)
                .EM("Lấy chi tiết danh mục thành công")
                .result(categoryService.getDetailCategory(categoryId))
                .build();
    }

    @GetMapping("/get-all")
    public ApiResponse<List<Category>> getAllCategory() {
        return ApiResponse.<List<Category>>builder()
                .EC(0)
                .EM("Lấy tất cả danh mục thành công")
                .result(categoryService.getAllCategory())
                .build();
    }

    @GetMapping("/get-sub/{id}")
    public ApiResponse<List<Category>> getSubCategory(@PathVariable("id") String categoryId) {
        return ApiResponse.<List<Category>>builder()
                .EC(0)
                .EM("Lấy danh mục con thành công")
                .result(categoryService.getSubCategory(categoryId))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ApiResponse<CategoryUpdateResponse> updateCategory(@PathVariable("id") String categoryId,
                                                              @RequestBody CategoryUpdateRequest request) {
        return ApiResponse.<CategoryUpdateResponse>builder()
                .EC(0)
                .EM("Cập nhật danh mục thành công")
                .result(categoryService.updateCategory(categoryId, request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable("id") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa danh mục thành công")
                .build();
    }
}
