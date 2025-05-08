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
    USER_EXISTED(1, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NON_EXISTED(1, "Không tìm thấy người dùng", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1,"Sai mật khẩu", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1, "Mã OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1,"Mật khẩu cũ không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_INDEX(1, "Chỉ số không hợp lệ", HttpStatus.BAD_REQUEST),
    DISCOUNT_CODE_EXISTED(1, "Mã giảm giá đã tồn tại", HttpStatus.BAD_REQUEST),
    DISCOUNT_NON_EXISTED(2, "Không tìm thấy mã giảm giá", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1, "Không tìm thấy danh mục sản phẩm", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1, "Danh mục sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE(1, "Giá trị enum không hợp lệ", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
