package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.ChatRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.BotSuggestResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.OpenAIService;
import com.s2tv.sportshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/openai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PostMapping("/")
    public ApiResponse<BotSuggestResponse> chatWithBot(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ChatRequest chatRequest) {

        String userId = (userPrincipal != null) ? userPrincipal.getUser().getId() : null;
        BotSuggestResponse reply = openAIService.chatWithBot(
                chatRequest.getMessage(), userId, chatRequest.getHistory());

        return ApiResponse.<BotSuggestResponse>builder()
                .EC(0)
                .EM("Reply thành công")
                .result(reply)
                .build();
    }


    @GetMapping("/")
    public ApiResponse<String> searchProductFilter(@RequestParam("message") String chatRequest, @AuthenticationPrincipal UserPrincipal userPrincipal){
        String reply = openAIService.searchProductFilter(chatRequest);
        if (userPrincipal !=null){
            openAIService.appendSearchHistory(userPrincipal.getUser().getId(), chatRequest, reply);
        }
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Reply thành công")
                .result(reply)
                .build();
    }

    @GetMapping("/compare")
    public ApiResponse<String> compare(@RequestParam String productIdA, @RequestParam String productIdB ){
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("So sánh sản phẩm thành công")
                .result(openAIService.compareProducts(productIdA, productIdB))
                .build();
    }
}
