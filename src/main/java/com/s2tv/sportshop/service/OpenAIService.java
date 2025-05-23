package com.s2tv.sportshop.service;


import com.s2tv.sportshop.config.WebClientConfig;
import com.s2tv.sportshop.dto.request.ProductFilterRequest;
import com.s2tv.sportshop.dto.response.OpenAIResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.model.Category;
import com.s2tv.sportshop.model.ChatHistory;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.model.SearchHistory;
import com.s2tv.sportshop.model.User;
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

    @Value("${openai.api_key}")
    private String openAiApiKey;

    public String chatWithBot(String message, String userId, List<ChatHistory.Message> tempHistory) {
        List<ChatHistory.Message> messages;

        if (userId != null) {
            ChatHistory chat = chatHistoryRepository.findByUserId(userId)
                    .orElseGet(() -> ChatHistory.builder()
                            .userId(userId)
                            .messages(new ArrayList<>(List.of(
                                    new ChatHistory.Message("system", "Bạn là trợ lý bán hàng của cửa hàng bán đồ thể thao WTM.")
                            )))
                            .build());

            chat.getMessages().add(new ChatHistory.Message("user", message));

            String reply = callOpenAItoChat(chat.getMessages(), "gpt-4");

            chat.getMessages().add(new ChatHistory.Message("assistant", reply));
            chat.setUpdatedAt(new Date());
            chatHistoryRepository.save(chat);

            return reply;
        } else {
            messages = new ArrayList<>();
            messages.add(new ChatHistory.Message("system", "Bạn là một trợ lý bán hàng của cửa hàng bán đồ thể thao WTM."));

            if (tempHistory != null) messages.addAll(tempHistory);

            messages.add(new ChatHistory.Message("user", message));

            return callOpenAItoChat(messages, "gpt-4");
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
                Dựa vào thông tin message, hãy tạo string như sau:
                {"category": string (phải nằm trong danh sách: [%s]; và lấy category_type)
                "category_gender": string (phải nằm trong danh sách: [Nam, Nữ, Unisex], nếu không có thì để trống)
                "category_sub": string (nếu có)(phải nằm trong danh sách: [%s]; và lấy category_type)
                "price_min": number (nếu có - không có thì không gửi)
                "price_max": number (nếu có - không có thì không gửi)
                "product_color": string (nếu có - viết hoa chữ đầu)
                "product_brand - viết hoa chữ đầu": string (nếu có)}
                Chỉ trả về string.
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

        String result = callOpenAItoProductFilter(messages, "gpt-4");
        return Objects.equals(result, "có");
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

        if (!a.getProductCategory().equalsIgnoreCase(b.getProductCategory())) {
            throw new AppException(ErrorCode.COMPARE_ERROR);
        }

        String systemPrompt = """
        Bạn là chuyên gia tư vấn sản phẩm thể thao.
        Hãy so sánh hai sản phẩm sau về: tên, thương hiệu, danh mục, giá, giảm giá, nổi bật, đánh giá và kho còn lại.
        Viết so sánh ngắn gọn, nêu ưu nhược điểm mỗi sản phẩm và gợi ý khi nào nên chọn sản phẩm nào.
        Trả về đoạn văn, không dùng bullet point.
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
                a.getProductTitle(), a.getProductBrand(), a.getProductCategory(),
                a.getProductPrice(), a.getProductPercentDiscount(),
                a.isProductFamous() ? "Có" : "Không", a.getProductRate(), a.getProductCountInStock(),

                b.getProductTitle(), b.getProductBrand(), b.getProductCategory(),
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
