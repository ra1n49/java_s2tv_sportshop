package com.s2tv.sportshop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR), // 500
    INVALID_KEY(1, "Từ khóa không hợp lệ", HttpStatus.BAD_REQUEST), // 400
    MAX_UPLOAD_SIZE_EXCEEDED(1, "Kích thước tệp tải lên vượt quá giới hạn cho phép", HttpStatus.PAYLOAD_TOO_LARGE),
    MISSING_REQUEST_PART(1, "Thiếu trường dữ liệu bắt buộc", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER(1, "Thiếu tham số bắt buộc", HttpStatus.BAD_REQUEST),
    INVALID_JSON(1, "Dữ liệu JSON không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_IMG_REQUIRED(1, "Ảnh chính sản phẩm là bắt buộc", HttpStatus.BAD_REQUEST),
    COLOR_MAIN_IMG_REQUIRED(1, "Thiếu ảnh chính cho màu sắc sản phẩm", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAILED(1, "Lỗi khi upload ảnh", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(1, "Khong tim thay nguoi dung", HttpStatus.BAD_REQUEST),
    CART_EMPTY(1, "Gio hang trong", HttpStatus.BAD_REQUEST),
    PRODUCT_NOTFOUND(1, "Khong tim san pham", HttpStatus.BAD_REQUEST),
    ALREADY_FEEDBACK(1, "Đơn hàng đã được đánh giá", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
