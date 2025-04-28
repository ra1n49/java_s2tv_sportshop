package com.s2tv.sportshop.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.s2tv.sportshop.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleException(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setEC(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setEM(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException exception) {
        ApiResponse apiResponse = new ApiResponse();

        ErrorCode errorCode = exception.getErrorCode();
        apiResponse.setEC(errorCode.getCode());
        apiResponse.setEM(errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgument(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {

        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setEC(errorCode.getCode());
        apiResponse.setEM(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    // Kích thước mà vượt quá trong application thì báo lỗi ở đây
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
        ApiResponse apiResponse = new ApiResponse();

        // Error code for file size exceeded
        apiResponse.setEC(ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED.getCode());
        apiResponse.setEM(ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED.getMessage());

        return ResponseEntity
                .status(ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED.getStatusCode())
                .body(apiResponse);
    }


    // Test api thiếu trường nào bắt buộc sẽ báo ở đây
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestPart(MissingServletRequestPartException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setEC(ErrorCode.MISSING_REQUEST_PART.getCode());
        apiResponse.setEM(ErrorCode.MISSING_REQUEST_PART.getMessage() + ": " + exception.getRequestPartName());

        return ResponseEntity
                .status(ErrorCode.MISSING_REQUEST_PART.getStatusCode())
                .body(apiResponse);
    }

    // Test api thiếu trường nào bắt buộc sẽ báo ở đây
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setEC(ErrorCode.MISSING_REQUEST_PARAMETER.getCode());
        apiResponse.setEM(ErrorCode.MISSING_REQUEST_PART.getMessage() + ": " + exception.getParameterName());

        return ResponseEntity
                .status(ErrorCode.MISSING_REQUEST_PARAMETER.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse> handleJsonProcessingException(JsonProcessingException exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setEC(ErrorCode.INVALID_JSON.getCode());
        apiResponse.setEM(ErrorCode.INVALID_JSON.getMessage() + ": " + exception.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_JSON.getStatusCode())
                .body(apiResponse);
    }
}
