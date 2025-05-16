package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.FeedbackCreateRequest;
import com.s2tv.sportshop.dto.response.FeedbackResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.FeedbackMapper;
import com.s2tv.sportshop.model.Feedback;
import com.s2tv.sportshop.model.FeedbackMedia;
import com.s2tv.sportshop.model.Product;
import com.s2tv.sportshop.repository.FeedbackRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackService {

    FeedbackRepository feedbackRepository;
    CloudinaryService cloudinaryService;
    ProductRepository productRepository;
    FeedbackMapper feedbackMapper;
    OpenAIService openAIService;


    public FeedbackResponse createFeedback(FeedbackCreateRequest req) {
        if (openAIService.checkSensitiveFeedback(req.getContent())) {
            throw new AppException(ErrorCode.SENSITIVE_FEEDBACK);
        }

        List<String> imageUrls = req.getImages() != null
                ? uploadAll(req.getImages())
                : List.of();

        List<String> videoUrls = req.getVideos() != null
                ? uploadAll(req.getVideos())
                : List.of();

        FeedbackMedia media = FeedbackMedia.builder()
                .images(imageUrls)
                .videos(videoUrls)
                .build();

        Feedback feedback = Feedback.builder()
                .productId(req.getProductId())
                .color(req.getColor())
                .variant(req.getVariant())
                .orderId(req.getOrderId())
                .userId(req.getUserId())
                .content(req.getContent())
                .rating(req.getRating())
                .feedbackMedia(media)
                .repliedByAdmin(null)
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);

        List<Feedback> feedbackList = feedbackRepository.findByProductIdAndDeletedFalse(req.getProductId());

        double avgRating = feedbackList.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setProduct_rate(avgRating);
        productRepository.save(product);

        return feedbackMapper.toResponse(savedFeedback);
    }

    public List<String> uploadAll(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return List.of();
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                urls.add(cloudinaryService.uploadFeedback(file));
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi upload file: " + file.getOriginalFilename(), e);
            }
        }
        return urls;
    }

    public List<FeedbackResponse> getFeedbacks(String productId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductIdAndDeletedFalse(productId);
        return feedbackMapper.toResponseList(feedbacks);
    }
}