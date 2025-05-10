package com.s2tv.sportshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.request.ProductGetAllRequest;
import com.s2tv.sportshop.dto.request.ProductUpdateRequest;
import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.dto.response.ProductGetAllResponse;
import com.s2tv.sportshop.dto.response.ProductUpdateResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.ProductMapper;
import com.s2tv.sportshop.model.Category;
import com.s2tv.sportshop.model.Color;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.repository.CategoryRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductCreateResponse createProduct(
            ProductCreateRequest productRequest,
            String colorsJson,
            MultipartHttpServletRequest multipartRequest) throws JsonProcessingException {

        List<Color> colors = parseColorsFromJson(colorsJson);

        Product product = productMapper.toProduct(productRequest);
        product.setColors(colors);
        product.setProduct_selled(0);

        Category category = categoryRepository.findById(productRequest.getProduct_category())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        product.setProduct_category(category.getId());

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
        return productMapper.toProductCreateResponse(savedProduct);
    }

    private List<Color> parseColorsFromJson(String colorsJson) throws JsonProcessingException {
        if (colorsJson == null || colorsJson.isEmpty()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(colorsJson, new TypeReference<List<Color>>() {});
    }

    public ProductUpdateResponse updateProduct(
            String productId,
            ProductUpdateRequest productUpdateRequest,
            String colorJson,
            MultipartHttpServletRequest request
    ) throws JsonProcessingException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.updateProductFromRequest(productUpdateRequest, existingProduct);

        List<Color> updateColors = colorJson != null && !colorJson.isEmpty()
                ? parseColorsFromJson(colorJson)
                : existingProduct.getColors();
        existingProduct.setColors(updateColors);

        Map<String, List<String>> uploadFiles = uploadImages(existingProduct, request);
        if (!uploadFiles.isEmpty()) {
            updateProductImages(existingProduct, uploadFiles);
        }

        double product_price = existingProduct.getProduct_price();
        List<Double> allPrices = existingProduct.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .map(variant -> variant.getVariant_price())
                .toList();

        if(!allPrices.isEmpty()) {
            product_price = allPrices.stream()
                    .mapToDouble(x -> x)
                    .min()
                    .orElse(existingProduct.getProduct_price());
        }

        int product_countInStock = existingProduct.getProduct_countInStock();
        product_countInStock = existingProduct.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .mapToInt(variant -> variant.getVariant_countInStock())
                .sum();

        existingProduct.setProduct_price(product_price);
        existingProduct.setProduct_countInStock(product_countInStock);

        Product saveProduct = productRepository.save(existingProduct);
        return productMapper.toProductUpdateResponse(saveProduct);
    }

    public void deleteProduct(String productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xóa ảnh chính sản phẩm
        if(existingProduct.getProduct_img() != null && !existingProduct.getProduct_img().isEmpty()) {
            cloudinaryService.deleteFile(existingProduct.getProduct_img(), "image");
        }

        // Xóa ảnh từng biển thể
        if(existingProduct.getColors() != null) {
            for(Color color : existingProduct.getColors()) {
                if(color.getImgs() != null) {
                    if(color.getImgs().getImg_main() != null && !color.getImgs().getImg_main().isEmpty()) {
                        cloudinaryService.deleteFile(color.getImgs().getImg_main(), "image");
                    }

                    if(color.getImgs().getImg_subs() != null && !color.getImgs().getImg_subs().isEmpty()) {
                        for(String subImg : color.getImgs().getImg_subs()) {
                            cloudinaryService.deleteFile(subImg, "image");
                        }
                    }
                }
            }
        }

        productRepository.delete(existingProduct);
    }

    public Product getDetailsProduct(String productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return existingProduct;
    }

    public ProductGetAllResponse getAllProduct(ProductGetAllRequest request) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = new ArrayList<>();

        // category_gender
        List<String> genderFilter = (request.getCategory_gender() != null && !request.getCategory_gender().isEmpty())
                ? request.getCategory_gender()
                : List.of("Nam", "Nữ", "Unisex");

        // Xử lý categoryIds
        Set<String> categoryIds = new HashSet<>();
        if ((genderFilter.size() != 3) &&
                (request.getCategory() == null || request.getCategory().isEmpty()) &&
                (request.getCategory_sub() == null || request.getCategory_sub().isEmpty())) {
            List<Category> categories = categoryRepository.findByCategoryGenderIn(genderFilter);
            categories.forEach(cat -> categoryIds.add(cat.getId()));
        }

        // Xử lý sub-category mapping
        List<Category> subCategories = new ArrayList<>();
        if (request.getCategory_sub() != null && !request.getCategory_sub().isEmpty()) {
            subCategories = categoryRepository.findByCategoryTypeInAndCategoryGenderIn(
                    request.getCategory_sub(), genderFilter);
        }

        Map<String, List<String>> parentToSubs = new HashMap<>();
        for (Category subCat : subCategories) {
            parentToSubs.computeIfAbsent(subCat.getCategoryParentId(), k -> new ArrayList<>()).add(subCat.getId());
        }

        if (request.getCategory() == null && request.getCategory_sub() != null) {
            for (Category subCat : subCategories) {
                categoryIds.add(subCat.getId());
            }
        }

        if (request.getCategory() != null) {
            List<Category> parents = categoryRepository.findByCategoryTypeInAndCategoryGenderIn(
                    request.getCategory(), genderFilter);
            for (Category parent : parents) {
                List<String> subs = parentToSubs.getOrDefault(parent.getId(), Collections.emptyList());
                if (!subs.isEmpty()) {
                    categoryIds.addAll(subs);
                } else {
                    categoryIds.add(parent.getId());
                    List<Category> children = categoryRepository.findByCategoryParentIdInAndCategoryGenderIn(
                            List.of(parent.getId()), genderFilter
                    );
                    children.forEach(child -> categoryIds.add(child.getId()));
                }
            }
        }

        if (!categoryIds.isEmpty()) {
            andCriteria.add(Criteria.where("product_category").in(categoryIds));
        }

        if (request.getPrice_min() != null || request.getPrice_max() != null) {
            Criteria priceCriteria = Criteria.where("product_price");
            if (request.getPrice_min() != null) {
                priceCriteria = priceCriteria.gte(request.getPrice_min());
            }
            if (request.getPrice_max() != null) {
                priceCriteria = priceCriteria.lte(request.getPrice_max());
            }
            andCriteria.add(priceCriteria);
        }

        if (request.getProduct_color() != null && !request.getProduct_color().isEmpty()) {
            andCriteria.add(Criteria.where("colors.color_name").in(request.getProduct_color()));
        }

        if (request.getProduct_brand() != null && !request.getProduct_brand().isEmpty()) {
            andCriteria.add(Criteria.where("product_brand").in(request.getProduct_brand()));
        }

        if (!andCriteria.isEmpty()) {
            criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);
        List<Product> products = mongoTemplate.find(query, Product.class);
        long total = mongoTemplate.count(query, Product.class);

        return ProductGetAllResponse.builder()
                .total((int) total)
                .products(products)
                .build();
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
            List<String> colorMainImgs = filesMap.get(colorMainKey);
            if (colorMainImgs == null || colorMainImgs.isEmpty()) {
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

    private void updateProductImages(Product product, Map<String, List<String>> filesMap) {
        // Cập nhật ảnh chính sản phẩm nếu có
        if(filesMap.containsKey("product_img") && !filesMap.get("product_img").isEmpty()) {
            product.setProduct_img(filesMap.get("product_img").get(0));
        }

        List<Color> colors = product.getColors();
        for(int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);
            if(color.getImgs() == null) {
                color.setImgs(new Color.Img());
            }

            Color.Img img = color.getImgs();
            String colorMainKey = "color_img_" + i + "_main";
            if(filesMap.containsKey(colorMainKey) && !filesMap.get(colorMainKey).isEmpty()) {
                img.setImg_main(filesMap.get(colorMainKey).get(0));
            }

            String colorSubsKey = "color_img_" + i + "_subs";
            if(filesMap.containsKey(colorSubsKey) && !filesMap.get(colorSubsKey).isEmpty()) {
                img.setImg_subs(filesMap.get(colorSubsKey));
            }
        }
    }
}