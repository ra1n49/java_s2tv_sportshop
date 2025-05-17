package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.CategoryCreateRequest;
import com.s2tv.sportshop.dto.request.CategoryUpdateRequest;
import com.s2tv.sportshop.dto.response.CategoryCreateResponse;
import com.s2tv.sportshop.dto.response.CategoryUpdateResponse;
import com.s2tv.sportshop.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryCreateRequest request);

    @Mapping(source = "categoryGender", target = "categoryGender")
    @Mapping(source = "categoryType", target = "categoryType")
    @Mapping(source = "categoryParentId", target = "categoryParentId")
    @Mapping(source = "categoryLevel", target = "categoryLevel")
    CategoryCreateResponse toCategoryCreateResponse(Category category);

    default void updateCategoryFromRequest(CategoryUpdateRequest request, @MappingTarget Category existingCategory) {
        if (request.getCategoryType() != null) {
            existingCategory.setCategoryType(request.getCategoryType());
        }
        if (request.getCategoryGender() != null) {
            existingCategory.setCategoryGender(request.getCategoryGender());
        }
        if (request.getCategoryParentId() != null) {
            existingCategory.setCategoryParentId(request.getCategoryParentId());
        }
        if (request.getCategoryLevel() != null) {
            existingCategory.setCategoryLevel(request.getCategoryLevel());
        }
    };
    @Mapping(source = "categoryGender", target = "categoryGender")
    @Mapping(source = "categoryType", target = "categoryType")
    @Mapping(source = "categoryParentId", target = "categoryParentId")
    @Mapping(source = "categoryLevel", target = "categoryLevel")
    CategoryUpdateResponse toCategoryUpdateResponse(Category category);

}
