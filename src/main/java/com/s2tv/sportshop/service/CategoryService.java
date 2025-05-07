package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.CategoryCreateRequest;
import com.s2tv.sportshop.dto.request.CategoryUpdateRequest;
import com.s2tv.sportshop.dto.response.CategoryCreateResponse;
import com.s2tv.sportshop.dto.response.CategoryUpdateResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.CategoryMapper;
import com.s2tv.sportshop.model.Category;
import com.s2tv.sportshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryCreateResponse createCategory(CategoryCreateRequest request) {
        Optional<Category> existingCategory = categoryRepository
                .findByCategoryTypeAndCategoryGender(request.getCategoryType(), request.getCategoryGender());

        if (existingCategory.isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category saved = categoryRepository.save(categoryMapper.toCategory(request));

        return categoryMapper.toCategoryCreateResponse(saved);
    }

    public Category getDetailCategory(String categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return existingCategory;
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public List<Category> getSubCategory(String categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryRepository.findByCategoryParentId(categoryId);
    }

    public CategoryUpdateResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryMapper.updateCategoryFromRequest(request, existingCategory);

        Optional<Category> existingAfterUpdate = categoryRepository
                .findByCategoryTypeAndCategoryGenderAndIdNot(
                        existingCategory.getCategoryType(),
                        existingCategory.getCategoryGender(),
                        existingCategory.getId()
                );

        if (existingAfterUpdate.isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category saved = categoryRepository.save(existingCategory);
        return categoryMapper.toCategoryUpdateResponse(saved);
    }

    public void deleteCategory(String categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(existingCategory);
    }
}
