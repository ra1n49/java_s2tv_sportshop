package com.s2tv.sportshop.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2tv.sportshop.config.WebClientConfig;
import com.s2tv.sportshop.dto.request.ProductFilterRequest;
import com.s2tv.sportshop.dto.request.ProductGetAllRequest;
import com.s2tv.sportshop.dto.response.*;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.repository.CategoryRepository;
import com.s2tv.sportshop.repository.ChatHistoryRepository;
import com.s2tv.sportshop.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final ChatHistoryRepository chatHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final WebClient openAiClient;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Value("${openai.api_key}")
    private String openAiApiKey;

    public BotSuggestResponse chatWithBot(String message, String userId, List<ChatHistory.Message> tempHistory) {
        boolean isSuggest = message.toLowerCase().contains("tư vấn") || message.toLowerCase().contains("gợi ý");
        List<ProductShortResponse> shortProducts = List.of();
        String reply;
        System.out.println(userId);
        System.out.println(isSuggest);
        if (isSuggest) {
            String filterJson = searchProductFilter(message);
            ProductFilter filter = parseProductFilter(filterJson);
            ProductGetAllRequest.ProductGetAllRequestBuilder builder = ProductGetAllRequest.builder();

            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
                builder.category(List.of(filter.getCategory()));
            }
            if (filter.getCategoryGender() != null && !filter.getCategoryGender().isBlank()) {
                builder.categoryGender(List.of(filter.getCategoryGender()));
            }
            if (filter.getCategorySub() != null && !filter.getCategorySub().isBlank()) {
                builder.categorySub(List.of(filter.getCategorySub()));
            }
            if (filter.getPriceMin() != null) builder.priceMin(filter.getPriceMin().doubleValue());
            if (filter.getPriceMax() != null) builder.priceMax(filter.getPriceMax().doubleValue());
            if (filter.getProductColor() != null && !filter.getProductColor().isBlank()) {
                builder.productColor(List.of(filter.getProductColor()));
            }
            if (filter.getProductBrand() != null && !filter.getProductBrand().isBlank()) {
                builder.productBrand(List.of(filter.getProductBrand()));
            }

            ProductGetAllRequest request = builder.build();
            ProductGetAllResponse response = productService.getAllProduct(request);
            List<ProductGetDetailsResponse> products = response.getProducts();
            List<ProductGetDetailsResponse> showProducts = products.subList(0, Math.min(3, products.size()));

            StringBuilder sb = new StringBuilder();
            sb.append("Bạn là trợ lý bán hàng WTM. Khách hỏi: \"").append(message).append("\".");
            if (response.getTotal() == 0) {
                sb.append(" Hiện tại không tìm thấy sản phẩm phù hợp với yêu cầu này. "
                        + "Chỉ trả lời đúng câu này, tuyệt đối không thêm gì khác.");
            } else {
                sb.append("""
                    Danh sách sản phẩm phù hợp đã được hệ thống gửi cho khách. 
                    YÊU CẦU: 
                    - KHÔNG liệt kê lại tên hoặc mô tả bất kỳ sản phẩm nào.
                    - KHÔNG giới thiệu lại sản phẩm, KHÔNG đưa ra đánh giá tổng thể.
                    - KHÔNG viết lại danh sách.
                    - Chỉ được phép nói 1 câu ngắn gọn, ví dụ: "Bạn có thể xem chi tiết các sản phẩm phía dưới và lựa chọn sản phẩm phù hợp nhất." 
                    - KHÔNG trả lời thêm bất cứ điều gì khác, KHÔNG quảng cáo.
                    """);
            }


            System.out.println(sb);
            List<ChatHistory.Message> promptMsg = List.of(
                    new ChatHistory.Message("system", sb.toString())
            );
            reply = callOpenAItoChat(promptMsg, "gpt-4");

            shortProducts = showProducts.stream()
                    .map(p -> ProductShortResponse.builder()
                            .id(p.getId())
                            .productTitle(p.getProductTitle())
                            .productImg(p.getProductImg())
                            .productPrice(p.getProductPrice())
                            .build())
                    .toList();

            if (userId != null) {
                ChatHistory chat = chatHistoryRepository.findByUserId(userId)
                        .orElseGet(() -> ChatHistory.builder()
                                .userId(userId)
                                .messages(new ArrayList<>(List.of(
                                        new ChatHistory.Message("system", "Bạn là trợ lý bán hàng của cửa hàng bán đồ thể thao WTM.")
                                )))
                                .build());
                chat.getMessages().add(new ChatHistory.Message("user", message));
                chat.getMessages().add(new ChatHistory.Message("assistant", reply));
                chat.setUpdatedAt(new Date());
                System.out.println(chat);
                chatHistoryRepository.save(chat);
            }
        } else {
            List<ChatHistory.Message> messages = new ArrayList<>();
            messages.add(new ChatHistory.Message("system", "Bạn là trợ lý bán hàng của cửa hàng bán đồ thể thao WTM."));
            if (tempHistory != null) messages.addAll(tempHistory);
            messages.add(new ChatHistory.Message("user", message));
            reply = callOpenAItoChat(messages, "gpt-4");
            shortProducts = List.of();

            if (userId != null) {
                ChatHistory chat = chatHistoryRepository.findByUserId(userId)
                        .orElseGet(() -> ChatHistory.builder()
                                .userId(userId)
                                .messages(new ArrayList<>(List.of(
                                        new ChatHistory.Message("system", "Bạn là trợ lý bán hàng của cửa hàng bán đồ thể thao WTM.")
                                )))
                                .build());
                chat.getMessages().add(new ChatHistory.Message("user", message));
                chat.getMessages().add(new ChatHistory.Message("assistant", reply));
                chat.setUpdatedAt(new Date());
                System.out.println(chat);
                chatHistoryRepository.save(chat);
            }
        }

        return BotSuggestResponse.builder()
                .message(reply)
                .products(shortProducts)
                .build();
    }


    public ProductFilter parseProductFilter(String filterJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(filterJson, ProductFilter.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON, "Không thể parse filter JSON: " + e.getMessage());
        }
    }


    public String searchProductFilter(String message){
        List<Category> level1 = categoryRepository.findByCategoryLevel(1);
        List<Category> level2 = categoryRepository.findByCategoryLevel(2);

        String parentCategories = level1.stream()
                .map(Category::getCategoryType)
                .distinct()
                .collect(Collectors.joining(", "));

        String subCategories = level2.stream()
                .map(Category::getCategoryType)
                .distinct()
                .collect(Collectors.joining(", "));

        String systemPrompt = String.format("""
            Dựa vào thông tin message của người dùng, hãy trích xuất filter theo định dạng sau:
            {
              "category": string (bắt buộc, phải là một giá trị trong danh sách: [%s]; lấy theo category_type, nếu message có lỗi chính tả hoặc dùng từ gần đúng thì hãy tự động chuyển đổi sang category phù hợp nhất),
              "category_gender": string (giới tính: Nam, Nữ, Unisex; nếu không có thì để trống),
              "category_sub": string (nếu có, nằm trong danh sách: [%s]; lấy category_type, cũng cần tự động sửa lỗi chính tả hoặc chọn gần đúng),
              "price_min": number (nếu có),
              "price_max": number (nếu có),
              "product_color": string (nếu có, viết hoa chữ cái đầu),
              "product_brand": string (nếu có, viết hoa chữ cái đầu)
            }
            Lưu ý:
            - Nếu không xác định được chính xác category từ message do lỗi chính tả, hãy cố gắng đoán và chuyển thành category phù hợp nhất trong danh sách.
            - Chỉ trả về chuỗi JSON, không giải thích gì thêm.
            """, parentCategories, subCategories);

        List<ProductFilterRequest.Message> messages = List.of(
                new ProductFilterRequest.Message("system", systemPrompt),
                new ProductFilterRequest.Message("user", message)
        );

        return callOpenAItoProductFilter(messages, "gpt-4");
    }


    private String callOpenAItoProductFilter(List<ProductFilterRequest.Message> messages, String model) {
        ProductFilterRequest request = new ProductFilterRequest(model, messages);

        OpenAIResponse response = openAiClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().getFirst().getMessage().getContent();
        }

        return "Có lỗi xảy ra";
    }

    public void appendSearchHistory(String userId, String message, String filters) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setMessage(message);
        searchHistory.setFilters(filters);
        searchHistory.setSearchedAt(new Date());

        if (user.getSearchhistory() == null) {
            user.setSearchhistory(new ArrayList<>());
        }

        user.getSearchhistory().add(searchHistory);

        userRepository.save(user);
    }



    public Boolean checkSensitiveFeedback(String feedbackContent) {
        String systemPrompt = """
        Bạn là một bộ lọc kiểm tra nội dung phản hồi của người dùng. 
        Hãy phân tích đoạn văn sau và trả lời chỉ bằng 1 từ: "có" nếu chứa nội dung nhạy cảm 
        (thô tục, bạo lực, xúc phạm, phân biệt chủng tộc, giới tính, chính trị...) hoặc "không" nếu an toàn.
        Không cần giải thích.
        """;

        List<ProductFilterRequest.Message> messages = List.of(
                new ProductFilterRequest.Message("system", systemPrompt),
                new ProductFilterRequest.Message("user", feedbackContent)
        );

        System.out.println("feedbackContent" + feedbackContent);
        String result = callOpenAItoProductFilter(messages, "gpt-4");
        System.out.println("result" + result);
        return "có".equalsIgnoreCase(result.trim());
    }

    private String callOpenAItoChat(List<ChatHistory.Message> messages, String model) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", messages
        );

        OpenAIResponse response = openAiClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().getFirst().getMessage().getContent();
        }

        return "Có lỗi xảy ra";
    }

    public String compareProducts(String productIdA, String productIdB) {
        Product a = productRepository.findById(productIdA)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm đầu tiên"));
        Product b = productRepository.findById(productIdB)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm thứ hai"));

        Category categoryA = categoryRepository.findById(a.getProductCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Không tìm thấy danh mục sản phẩm đầu tiên"));

        Category categoryB = categoryRepository.findById(b.getProductCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Không tìm thấy danh mục sản phẩm thứ hai"));

        if (!a.getProductCategory().equalsIgnoreCase(b.getProductCategory())) {
            throw new AppException(ErrorCode.COMPARE_ERROR);
        }

        String systemPrompt = """
        Bạn là chuyên gia tư vấn sản phẩm thể thao.
        So sánh hai sản phẩm sau về: tên, thương hiệu, danh mục, giá, giảm giá, nổi bật, đánh giá và kho còn lại.
        
        Hãy trả về nội dung so sánh chia thành 3 đoạn, mỗi đoạn nằm trong thẻ <p> với tiêu đề:
        - <p><strong>Thông tin chung:</strong>...</p>
        - <p><strong>So sánh điểm mạnh:</strong>...</p>
        - <p><strong>Gợi ý lựa chọn:</strong>...</p>
        
        Không dùng bullet point, không thêm văn bản bên ngoài các thẻ <p>.
        """;

        String userMessage = String.format("""
        Sản phẩm A:
        Tên: %s
        Thương hiệu: %s
        Danh mục: %s
        Giá: %.0f
        Giảm giá: %.0f%%
        Nổi bật: %s
        Đánh giá: %.1f sao
        Còn lại: %d sản phẩm

        Sản phẩm B:
        Tên: %s
        Thương hiệu: %s
        Danh mục: %s
        Giá: %.0f
        Giảm giá: %.0f%%
        Nổi bật: %s
        Đánh giá: %.1f sao
        Còn lại: %d sản phẩm
        """,
                a.getProductTitle(), a.getProductBrand(), categoryA.getCategoryType() + " - " + categoryA.getCategoryGender().name(),
                a.getProductPrice(), a.getProductPercentDiscount(),
                a.isProductFamous() ? "Có" : "Không", a.getProductRate(), a.getProductCountInStock(),

                b.getProductTitle(), b.getProductBrand(), categoryB.getCategoryType() + " - " + categoryB.getCategoryGender().name(),
                b.getProductPrice(), b.getProductPercentDiscount(),
                b.isProductFamous() ? "Có" : "Không", b.getProductRate(), b.getProductCountInStock()
        );

        List<ProductFilterRequest.Message> messages = List.of(
                new ProductFilterRequest.Message("system", systemPrompt),
                new ProductFilterRequest.Message("user", userMessage)
        );

        return callOpenAItoProductFilter(messages, "gpt-4");
    }
}
