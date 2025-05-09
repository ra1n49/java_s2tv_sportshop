package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.ProductUpdateRequest;
import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.response.ProductUpdateResponse;
import com.s2tv.sportshop.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Create
    @Mapping(target = "colors", ignore = true)
    @Mapping(target = "product_img", ignore = true)
    @Mapping(target = "product_price", ignore = true)
    @Mapping(target = "product_countInStock", ignore = true)
    @Mapping(target = "product_selled", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product_category", ignore = true)
    Product toProduct(ProductCreateRequest request);
    ProductCreateResponse toProductCreateResponse(Product product);


    // Update
    ProductUpdateResponse toProductUpdateResponse(Product product);
    // Nếu có nhập vào request thì thay đổi, không thì giữ nguyên hết để khỏi mất
    default void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product) {
        if (request == null) {
            return;
        }

        if (request.getProduct_title() != null) {
            product.setProduct_title(request.getProduct_title());
        }

        if (request.getProduct_category() != null) {
            product.setProduct_category(request.getProduct_category());
        }

        if (request.getProduct_brand() != null) {
            product.setProduct_brand(request.getProduct_brand());
        }

        if (request.getProduct_description() != null) {
            product.setProduct_description(request.getProduct_description());
        }

        if (request.getProduct_display() != null) {
            product.setProduct_display(request.getProduct_display());
        }

        if (request.getProduct_famous() != null) {
            product.setProduct_famous(request.getProduct_famous());
        }

        if (request.getProduct_rate() > 0) {
            product.setProduct_rate(request.getProduct_rate());
        }

        if (request.getProduct_percent_discount() > 0) {
            product.setProduct_percent_discount(request.getProduct_percent_discount());
        }
    }
}
