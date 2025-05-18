package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.ProductUpdateRequest;
import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.response.ProductGetDetailsResponse;
import com.s2tv.sportshop.dto.response.ProductUpdateResponse;
import com.s2tv.sportshop.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Create
    @Mapping(target = "colors", ignore = true)
    @Mapping(target = "productImg", ignore = true)
    @Mapping(target = "productPrice", ignore = true)
    @Mapping(target = "productCountInStock", ignore = true)
    @Mapping(target = "productSelled", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCategory", ignore = true)
    Product toProduct(ProductCreateRequest request);
    ProductCreateResponse toProductCreateResponse(Product product);


    // Update
    ProductUpdateResponse toProductUpdateResponse(Product product);
    // Nếu có nhập vào request thì thay đổi, không thì giữ nguyên hết để khỏi mất
    default void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product) {
        if (request == null) {
            return;
        }

        if (request.getProductTitle() != null) {
            product.setProductTitle(request.getProductTitle());
        }

        if (request.getProductCategory() != null) {
            product.setProductCategory(request.getProductCategory());
        }

        if (request.getProductBrand() != null) {
            product.setProductBrand(request.getProductBrand());
        }

        if (request.getProductDescription() != null) {
            product.setProductDescription(request.getProductDescription());
        }

        if (request.getProductDisplay() != null) {
            product.setProductDisplay(request.getProductDisplay());
        }

        if (request.getProductFamous() != null) {
            product.setProductFamous(request.getProductFamous());
        }

        if (request.getProductRate() > 0) {
            product.setProductRate(request.getProductRate());
        }

        if (request.getProductPercentDiscount() > 0) {
            product.setProductPercentDiscount(request.getProductPercentDiscount());
        }
    }

    @Mapping(target = "productCategory", ignore = true)
    ProductGetDetailsResponse toProductGetDetailsResponse(Product product);
}
