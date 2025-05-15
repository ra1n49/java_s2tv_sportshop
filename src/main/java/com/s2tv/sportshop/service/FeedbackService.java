package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.FeedbackCreateRequest;
import com.s2tv.sportshop.dto.response.FeedbackResponse;
import com.s2tv.sportshop.exception.AppException;
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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.s2tv.sportshop.exception.ErrorCode.FEEDBACK_NOTFOUND;
import static com.s2tv.sportshop.exception.ErrorCode.PRODUCT_NOTFOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackService {

    FeedbackRepository feedbackRepository;
    CloudinaryService cloudinaryService;
    ProductRepository productRepository;
    FeedbackMapper feedbackMapper;

    public FeedbackResponse createFeedback(FeedbackCreateRequest req) {
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
                .orElseThrow(() -> new AppException(PRODUCT_NOTFOUND));
        product.setProduct_rate(avgRating);
        productRepository.save(product);

        return feedbackMapper.toResponse(savedFeedback);
    }

    private List<String> uploadAll(MultipartFile[] files) {
        return List.of(files).stream()
                .map(file -> cloudinaryService.uploadFile(file))
                .collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbacks(String productId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductIdAndDeletedFalse(productId);
        return feedbackMapper.toResponseList(feedbacks);
    }

    public void deleteFeedback(String feedbackId) {
        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(FEEDBACK_NOTFOUND));
        fb.setDeleted(true);
        fb.setDeletedAt(new Date());
        feedbackRepository.save(fb);
    }
}