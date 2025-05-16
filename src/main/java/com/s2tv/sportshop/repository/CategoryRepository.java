package com.s2tv.sportshop.repository;

import com.s2tv.sportshop.enums.CategoryGender;
import com.s2tv.sportshop.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByCategoryGenderIn(List<String> genders);
    List<Category> findByCategoryTypeInAndCategoryGenderIn(List<String> types, List<String> genders);
    List<Category> findByCategoryParentIdInAndCategoryGenderIn(List<String> parentIds, List<String> genders);
    List<Category> findByCategoryParentId(String parentId);
    List<Category> findByCategoryLevel(int level);
    Optional<Category> findByCategoryTypeAndCategoryGender(String categoryType, CategoryGender categoryGender);
    Optional<Category> findByCategoryTypeAndCategoryGenderAndIdNot(String type, CategoryGender gender, String id);
}
