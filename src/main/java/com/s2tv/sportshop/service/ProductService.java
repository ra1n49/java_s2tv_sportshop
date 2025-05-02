package com.s2tv.sportshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.ProductMapper;
import com.s2tv.sportshop.model.Color;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductMapper productMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponse<ProductCreateResponse> createProduct(
            ProductCreateRequest productRequest,
            String colorsJson,
            MultipartHttpServletRequest multipartRequest) throws JsonProcessingException {

        List<Color> colors = parseColorsFromJson(colorsJson);

        Product product = productMapper.toProduct(productRequest);
        product.setColors(colors);
        product.setProduct_selled(0);

        Map<String, List<String>> uploadedFiles = uploadImages(product, multipartRequest);
        mapImagesToProduct(product, uploadedFiles);

        // Tính giá và số lượng tồn kho
        double product_price = 0;
        int product_countInStock = 0;

        List<Double> allPrices = product.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .map(variant -> variant.getVariant_price())
                .toList();

        // Tìm giá thấp nhất
        if (!allPrices.isEmpty()) {
            product_price = allPrices.stream()
                    .mapToDouble(Double::doubleValue)
                    .min()
                    .orElse(0);
        }

        // Tính tổng số lượng tồn kho
        product_countInStock = product.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .mapToInt(variant -> variant.getVariant_countInStock())
                .sum();

        product.setProduct_price(product_price);
        product.setProduct_countInStock(product_countInStock);

        Product savedProduct = productRepository.save(product);
        return ApiResponse.<ProductCreateResponse>builder()
                .EC(0)
                .EM("Tạo sản phẩm mới thành công")
                .result(productMapper.toProductResponse(savedProduct))
                .build();
    }

    private List<Color> parseColorsFromJson(String colorsJson) throws JsonProcessingException {
        if (colorsJson == null || colorsJson.isEmpty()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(colorsJson, new TypeReference<List<Color>>() {});
    }

    private Map<String, List<String>> uploadImages(Product product, MultipartHttpServletRequest request) {
        Map<String, List<String>> filesMap = new HashMap<>();

        try {
            // Upload ảnh chính của sản phẩm
            MultipartFile productMainImg = request.getFile("product_img");
            if (productMainImg != null && !productMainImg.isEmpty()) {
                String mainImgUrl = cloudinaryService.uploadFile(productMainImg, "products", "image");
                filesMap.put("product_img", List.of(mainImgUrl));
            }

            // Upload ảnh cho các màu sắc
            List<Color> colors = product.getColors();
            for (int i = 0; i < colors.size(); i++) {
                // Ảnh chính của màu
                String colorMainKey = "color_img_" + i + "_main";
                MultipartFile colorMainFile = request.getFile(colorMainKey);
                if (colorMainFile != null && !colorMainFile.isEmpty()) {
                    String colorMainUrl = cloudinaryService.uploadFile(colorMainFile, "products", "image");
                    filesMap.put(colorMainKey, List.of(colorMainUrl));
                }

                // Ảnh phụ của màu
                String colorSubsKey = "color_img_" + i + "_subs";
                List<MultipartFile> colorSubsFiles = request.getFiles(colorSubsKey);
                if (colorSubsFiles != null && !colorSubsFiles.isEmpty()) {
                    List<String> colorSubsList = new ArrayList<>();
                    for (MultipartFile subFile : colorSubsFiles) {
                        if (subFile != null && !subFile.isEmpty()) {
                            String subUrl = cloudinaryService.uploadFile(subFile, "products", "image");
                            colorSubsList.add(subUrl);
                        }
                    }
                    filesMap.put(colorSubsKey, colorSubsList);
                }
            }

            return filesMap;
        } catch (Exception e) {
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private void mapImagesToProduct(Product product, Map<String, List<String>> filesMap) {
        // Kiểm tra ảnh chính của sản phẩm
        if (filesMap.get("product_img") == null || filesMap.get("product_img").isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_IMG_REQUIRED);
        }

        // Gán ảnh chính cho sản phẩm
        product.setProduct_img(filesMap.get("product_img").get(0));

        // Gán ảnh cho từng màu
        List<Color> colors = product.getColors();
        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);
            Color.Img img = new Color.Img();

            // Gán ảnh chính cho màu
            String colorMainKey = "color_img_" + i + "_main";
            if (filesMap.get(colorMainKey) == null && filesMap.get(colorMainKey).isEmpty()) {
                throw new AppException(ErrorCode.COLOR_MAIN_IMG_REQUIRED);
            }
            img.setImg_main(filesMap.get(colorMainKey).get(0));

            // Gán các ảnh phụ cho màu
            String colorSubsKey = "color_img_" + i + "_subs";
            if (filesMap.get(colorSubsKey) != null) {
                img.setImg_subs(filesMap.get(colorSubsKey));
            } else {
                img.setImg_subs(new ArrayList<>());
            }

            color.setImgs(img);
        }
    }
}