package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.response.FeedbackMediaResponse;
import com.s2tv.sportshop.dto.response.FeedbackResponse;
import com.s2tv.sportshop.model.Feedback;
import com.s2tv.sportshop.model.FeedbackMedia;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackResponse toResponse(Feedback feedback);
    FeedbackMediaResponse toMediaResponse(FeedbackMedia media);
    List<FeedbackResponse> toResponseList(List<Feedback> feedbackList);
}
