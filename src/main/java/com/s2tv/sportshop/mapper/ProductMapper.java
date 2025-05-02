package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "colors", ignore = true)
    @Mapping(target = "product_img", ignore = true)
    @Mapping(target = "product_price", ignore = true)
    @Mapping(target = "product_countInStock", ignore = true)
    @Mapping(target = "product_selled", ignore = true)
    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductCreateRequest request);

    ProductCreateResponse toProductResponse(Product product);
}
