package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.ChatRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/openai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PostMapping("/")
    public ApiResponse<String> chatWithBot(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ChatRequest chatRequest){
        String userId = (userPrincipal != null) ? userPrincipal.getUser().getId() : null;
        String reply = openAIService.chatWithBot(chatRequest.getMessage(), userId, chatRequest.getHistory());
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Reply thành công")
                .result(reply)
                .build();
    }

    @GetMapping("/")
    public ApiResponse<String> searchProductFilter(@RequestBody ChatRequest chatRequest){
        String reply = openAIService.searchProductFilter(chatRequest.getMessage());
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Reply thành công")
                .result(reply)
                .build();
    }
}
