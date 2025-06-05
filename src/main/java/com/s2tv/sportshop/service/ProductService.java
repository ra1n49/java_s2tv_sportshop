package com.s2tv.sportshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2tv.sportshop.dto.request.ProductCreateRequest;
import com.s2tv.sportshop.dto.request.ProductGetAllRequest;
import com.s2tv.sportshop.dto.request.ProductUpdateRequest;
import com.s2tv.sportshop.dto.response.ProductCreateResponse;
import com.s2tv.sportshop.dto.response.ProductGetAllResponse;
import com.s2tv.sportshop.dto.response.ProductGetDetailsResponse;
import com.s2tv.sportshop.dto.response.ProductUpdateResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.ProductMapper;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.CategoryRepository;
import com.s2tv.sportshop.repository.OrderRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final CloudinaryService cloudinaryService;

    private final ProductMapper productMapper;

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PYTHON_API_URL = "http://localhost:8000/recommend";

    private final RestTemplate restTemplate = new RestTemplate();

    public ProductCreateResponse createProduct(
            ProductCreateRequest productRequest,
            String colorsJson,
            MultipartHttpServletRequest multipartRequest) throws JsonProcessingException {

        List<Color> colors = parseColorsFromJson(colorsJson);

        Product product = productMapper.toProduct(productRequest);
        product.setColors(colors);
        product.setProductSelled(0);

        Category category = categoryRepository.findById(productRequest.getProductCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        product.setProductCategory(category.getId());

        Map<String, List<String>> uploadedFiles = uploadImages(product, multipartRequest);
        mapImagesToProduct(product, uploadedFiles);

        // Tính giá và số lượng tồn kho
        double product_price = 0;
        int product_countInStock = 0;

        List<Double> allPrices = product.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .map(variant -> variant.getVariantPrice())
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
                .mapToInt(variant -> variant.getVariantCountInStock())
                .sum();

        product.setProductPrice(product_price);
        product.setProductCountInStock(product_countInStock);

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

        double product_price = existingProduct.getProductPrice();
        List<Double> allPrices = existingProduct.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .map(variant -> variant.getVariantPrice())
                .toList();

        if(!allPrices.isEmpty()) {
            product_price = allPrices.stream()
                    .mapToDouble(x -> x)
                    .min()
                    .orElse(existingProduct.getProductPrice());
        }

        int product_countInStock = existingProduct.getProductCountInStock();
        product_countInStock = existingProduct.getColors().stream()
                .flatMap(color -> color.getVariants().stream())
                .mapToInt(variant -> variant.getVariantCountInStock())
                .sum();

        existingProduct.setProductPrice(product_price);
        existingProduct.setProductCountInStock(product_countInStock);

        Product saveProduct = productRepository.save(existingProduct);
        return productMapper.toProductUpdateResponse(saveProduct);
    }

    public void deleteProduct(String productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xóa ảnh chính sản phẩm
        if(existingProduct.getProductImg() != null && !existingProduct.getProductImg().isEmpty()) {
            cloudinaryService.deleteFile(existingProduct.getProductImg(), "image");
        }

        // Xóa ảnh từng biển thể
        if(existingProduct.getColors() != null) {
            for(Color color : existingProduct.getColors()) {
                if(color.getImgs() != null) {
                    if(color.getImgs().getImgMain() != null && !color.getImgs().getImgMain().isEmpty()) {
                        cloudinaryService.deleteFile(color.getImgs().getImgMain(), "image");
                    }

                    if(color.getImgs().getImgSubs() != null && !color.getImgs().getImgSubs().isEmpty()) {
                        for(String subImg : color.getImgs().getImgSubs()) {
                            cloudinaryService.deleteFile(subImg, "image");
                        }
                    }
                }
            }
        }

        productRepository.delete(existingProduct);
    }

    public ProductGetDetailsResponse getDetailsProduct(String productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findById(existingProduct.getProductCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        ProductGetDetailsResponse response = productMapper.toProductGetDetailsResponse(existingProduct);
        response.setProductCategory(category);
        return response;
    }

    public ProductGetAllResponse getAllProduct(ProductGetAllRequest request) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = new ArrayList<>();

        // category_gender
        List<String> genderFilter = (request.getCategoryGender() != null && !request.getCategoryGender().isEmpty())
                ? request.getCategoryGender()
                : List.of("Nam", "Nữ", "Unisex");

        // Xử lý categoryIds
        Set<String> categoryIds = new HashSet<>();
        if ((genderFilter.size() != 3) &&
                (request.getCategory() == null || request.getCategory().isEmpty()) &&
                (request.getCategorySub() == null || request.getCategorySub().isEmpty())) {
            List<Category> categories = categoryRepository.findByCategoryGenderIn(genderFilter);
            categories.forEach(cat -> categoryIds.add(cat.getId()));
        }

        // Xử lý sub-category mapping
        List<Category> subCategories = new ArrayList<>();
        if (request.getCategorySub() != null && !request.getCategorySub().isEmpty()) {
            subCategories = categoryRepository.findByCategoryTypeInAndCategoryGenderIn(
                    request.getCategorySub(), genderFilter);
        }

        Map<String, List<String>> parentToSubs = new HashMap<>();
        for (Category subCat : subCategories) {
            parentToSubs.computeIfAbsent(subCat.getCategoryParentId(), k -> new ArrayList<>()).add(subCat.getId());
        }

        if (request.getCategory() == null && request.getCategorySub() != null) {
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
            andCriteria.add(Criteria.where("productCategory").in(categoryIds));
        }

        if (request.getPriceMin() != null || request.getPriceMax() != null) {
            Criteria priceCriteria = Criteria.where("productPrice");
            if (request.getPriceMin() != null) {
                priceCriteria = priceCriteria.gte(request.getPriceMin());
            }
            if (request.getPriceMax() != null) {
                priceCriteria = priceCriteria.lte(request.getPriceMax());
            }
            andCriteria.add(priceCriteria);
        }

        if (request.getProductColor() != null && !request.getProductColor().isEmpty()) {
            andCriteria.add(Criteria.where("colors.colorName").in(request.getProductColor()));
        }

        if (request.getProductBrand() != null && !request.getProductBrand().isEmpty()) {
            andCriteria.add(Criteria.where("productBrand").in(request.getProductBrand()));
        }

        if (!andCriteria.isEmpty()) {
            criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);
        List<Product> rawProducts = mongoTemplate.find(query, Product.class);

        Set<String> categoryIdsUsed = rawProducts.stream()
                .map(Product::getProductCategory)
                .collect(Collectors.toSet());

        Map<String, Category> categoryMap = categoryRepository.findAllById(categoryIdsUsed).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        List<ProductGetDetailsResponse> products = rawProducts.stream().map(product -> {
            ProductGetDetailsResponse response = productMapper.toProductGetDetailsResponse(product);

            Category cat = categoryMap.get(product.getProductCategory());
            response.setProductCategory(cat);

            return response;
        }).collect(Collectors.toList());

        long total = mongoTemplate.count(query, Product.class);

        return ProductGetAllResponse.builder()
                .total((int) total)
                .products(products)
                .build();
    }

    public List<ProductGetDetailsResponse> getRecommendedProducts(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        List<OrderProduct> purchasedProducts = orderRepository.findByUserId(userId).stream()
                .flatMap(order -> order.getProducts().stream())
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("user_id", user.getId());

        List<Map<String, String>> searchHistory = Optional.ofNullable(user.getSearchhistory())
                .orElse(Collections.emptyList())
                .stream()
                .map(sh -> Map.of("keyword", sh.getMessage()))
                .collect(Collectors.toList());
        body.put("search_history", searchHistory);

        List<Map<String, String>> purchased = purchasedProducts.stream()
                .map(p -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("product_id", p.getProductId());
                    map.put("category", p.getCategoryId());

                    Product prod = productRepository.findById(p.getProductId()).orElse(null);
                    if (prod != null) {
                        map.put("title", prod.getProductTitle() != null ? prod.getProductTitle() : "");
                        map.put("brand", prod.getProductBrand() != null ? prod.getProductBrand() : "");
                        map.put("description", prod.getProductDescription() != null ? prod.getProductDescription() : "");
                    } else {
                        map.put("title", "");
                        map.put("brand", "");
                        map.put("description", "");
                    }

                    if (p.getCategoryId() != null) {
                        categoryRepository.findById(p.getCategoryId()).ifPresent(category -> {
                            map.put("category_type", category.getCategoryType() != null ? category.getCategoryType() : "");
                        });
                    } else {
                        map.put("category_type", "");
                    }

                    return map;
                }).collect(Collectors.toList());

        body.put("purchased_products", purchased);

        System.out.println("User ID: " + body.get("user_id"));
        System.out.println("Search History: " + body.get("search_history"));
        System.out.println("Purchased Products: " + body.get("purchased_products"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_API_URL, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map respBody = response.getBody();
            if (respBody != null && respBody.containsKey("recommended_product_ids")) {
                List<String> recommendedIds = (List<String>) respBody.get("recommended_product_ids");

                return recommendedIds.stream()
                        .map(this::getDetailsProduct)
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
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
        product.setProductImg(filesMap.get("product_img").get(0));

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
            img.setImgMain(filesMap.get(colorMainKey).get(0));

            // Gán các ảnh phụ cho màu
            String colorSubsKey = "color_img_" + i + "_subs";
            if (filesMap.get(colorSubsKey) != null) {
                img.setImgSubs(filesMap.get(colorSubsKey));
            } else {
                img.setImgSubs(new ArrayList<>());
            }

            color.setImgs(img);
        }
    }

    private void updateProductImages(Product product, Map<String, List<String>> filesMap) {
        // Cập nhật ảnh chính sản phẩm nếu có
        if(filesMap.containsKey("product_img") && !filesMap.get("product_img").isEmpty()) {
            product.setProductImg(filesMap.get("product_img").get(0));
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
                img.setImgMain(filesMap.get(colorMainKey).get(0));
            }

            String colorSubsKey = "color_img_" + i + "_subs";
            if(filesMap.containsKey(colorSubsKey) && !filesMap.get(colorSubsKey).isEmpty()) {
                img.setImgSubs(filesMap.get(colorSubsKey));
            }
        }
    }
}