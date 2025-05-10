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

    @Mapping(source = "categoryGender", target = "category_gender")
    @Mapping(source = "categoryType", target = "category_type")
    @Mapping(source = "categoryParentId", target = "category_parent_id")
    @Mapping(source = "categoryLevel", target = "category_level")
    CategoryCreateResponse toCategoryCreateResponse(Category category);

    void updateCategoryFromRequest(CategoryUpdateRequest request, @MappingTarget Category existingCategory);
    @Mapping(source = "categoryGender", target = "category_gender")
    @Mapping(source = "categoryType", target = "category_type")
    @Mapping(source = "categoryParentId", target = "category_parent_id")
    @Mapping(source = "categoryLevel", target = "category_level")
    CategoryUpdateResponse toCategoryUpdateResponse(Category category);

}
