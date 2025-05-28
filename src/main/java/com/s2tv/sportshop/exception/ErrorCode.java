package com.s2tv.sportshop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR), // 500
    UNAUTHORIZED(401, "Không có quyền truy cập", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1, "Từ khóa không hợp lệ", HttpStatus.BAD_REQUEST), // 400
    MAX_UPLOAD_SIZE_EXCEEDED(1, "Kích thước tệp tải lên vượt quá giới hạn cho phép", HttpStatus.PAYLOAD_TOO_LARGE),
    MISSING_REQUEST_PART(1, "Thiếu trường dữ liệu bắt buộc", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER(1, "Thiếu tham số bắt buộc", HttpStatus.BAD_REQUEST),
    INVALID_JSON(1, "Dữ liệu JSON không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_IMG_REQUIRED(1, "Ảnh chính sản phẩm là bắt buộc", HttpStatus.BAD_REQUEST),
    COLOR_MAIN_IMG_REQUIRED(1, "Thiếu ảnh chính cho màu sắc sản phẩm", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAILED(1, "Lỗi khi upload ảnh", HttpStatus.INTERNAL_SERVER_ERROR),

    CART_EMPTY(1, "Giỏ hàng trống", HttpStatus.BAD_REQUEST),
    MIN_QUANTITY_REACHED(1, "Số lượng tối thiểu là 1, không thể giảm thêm", HttpStatus.BAD_REQUEST),
    STOCK_LIMIT_EXCEEDED(1, "Số lượng vượt quá tồn kho", HttpStatus.BAD_REQUEST),

    ALREADY_FEEDBACK(1, "Đơn hàng đã được đánh giá", HttpStatus.BAD_REQUEST),
    ORDER_EMPTY(1, "Đơn hàng không có sản phẩm nào", HttpStatus.BAD_REQUEST),
    ORDER_PRODUCT_OUT_OF_STOCK(1, "Sản phẩm không đủ tồn kho", HttpStatus.BAD_REQUEST),
    ORDER_PRODUCTS_REQUIRED(1, "Danh sách sản phẩm trong đơn hàng không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_DELIVERY_FEE(1, "Phí vận chuyển không hợp lệ", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_PRODUCT_QUANTITY(1006, "Số lượng sản phẩm phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1, "Trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ORDER_ACCESS(2, "Bạn không có quyền truy cập đơn hàng này", HttpStatus.FORBIDDEN),
    CANCEL_CONDITION_FAILED(1, "Không thể hủy đơn hàng vì trạng thái không hợp lệ", HttpStatus.BAD_REQUEST),
    ORDERCODE_IS_REQUIRED(1, "Mã đơn hàng là bắt buộc", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_ORDER_STATUS(1, "Không thể cập nhật trạng thái đơn hàng này", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_ORDER(1, "Không thể hủy đơn hàng", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    GOOGLE_ACCOUNT_EXISTED(1, "Tài khoản Google này đã được đăng ký", HttpStatus.BAD_REQUEST),
    USER_ID_IS_REQUIRED(1, "Mã khách hàng là bắt buộc", HttpStatus.BAD_REQUEST),
    USER_NON_EXISTED(1, "Không tìm thấy người dùng", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1,"Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1, "Mã OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1,"Mật khẩu cũ không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_INDEX(1, "Chỉ số không hợp lệ", HttpStatus.BAD_REQUEST),
    DISCOUNT_CODE_EXISTED(1, "Mã giảm giá đã tồn tại", HttpStatus.BAD_REQUEST),
    DISCOUNT_NON_EXISTED(1, "Không tìm thấy mã giảm giá", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1, "Không tìm thấy danh mục sản phẩm", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1, "Danh mục sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE(1, "Giá trị enum không hợp lệ", HttpStatus.BAD_REQUEST),
    FAVORITE_NOT_FOUND(1, "Không tìm thấy danh sách sản phẩm yêu thích", HttpStatus.NOT_FOUND),
    FAVORITE_EMPTY(1, "Danh sách sản phẩm yêu thích trống", HttpStatus.OK),
    INVALID_DISCOUNT_COMBINATION(1, "Áp dụng mã giảm giá không hợp lệ", HttpStatus.BAD_REQUEST),

    INSUFFICIENT_PRODUCT_QUANTITY(1, "Sản phẩm không đủ số lượng", HttpStatus.BAD_REQUEST),
    SHIPPING_ADDRESS_NOT_FOUND(1, "Địa chỉ giao hàng không tồn tại", HttpStatus.NOT_FOUND),
    DUPLICATE_DISCOUNT_TYPE(1, "Chỉ được áp dụng tối đa 1 mã giảm giá sản phẩm và 1 mã giảm giá vận chuyển", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_FOUND(1, "Mã giảm giá không tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_REQUIRE(1, "Sản phẩm là bắt buộc", HttpStatus.BAD_REQUEST),
    SHIPPINGADDRESS_REQUIRE(1, "Địa chỉ là bắt buộc", HttpStatus.BAD_REQUEST),
    PAYMENTMETHOD_REQUIRE(1, "Phương thức thanh toán là bắt buộc", HttpStatus.BAD_REQUEST),
    COLOR_NOT_FOUND(1, "Không tìm thấy màu", HttpStatus.BAD_REQUEST),
    VARIANT_NOT_FOUND(1, "Không tìm thấy size", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(1, "Sản phẩm đã hết hàng", HttpStatus.BAD_REQUEST),
    FEEDBACK_NOT_FOUND(1, "Không tìm thấy feedback", HttpStatus.BAD_REQUEST),
    CREATE_PAYMENT_FAILED(1, "Tạo thông tin thanh toán không thành công", HttpStatus.BAD_REQUEST),
    INVALID_WEBHOOK_SIGNATURE(1, "Chữ ký webhook không hợp lệ", HttpStatus.FORBIDDEN),
    SENSITIVE_FEEDBACK(1,"Bình luận không phù hợp", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOTFOUND(1, "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    STORE_INFO_REQUIRED(1, "Thông tin cửa hàng là bắt buộc", HttpStatus.BAD_REQUEST),
    STORE_NOT_FOUND(1, "Cửa hàng không tồn tại", HttpStatus.BAD_REQUEST),
    FEEDBACK_ALREADY_EXIST(1, "Bạn đã đánh giá sản phẩm trong đơn hàng này rồi", HttpStatus.BAD_REQUEST),
    COMPARE_ERROR(1, "Hai sản phẩm không thuộc cùng danh mục, không thể so sánh.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN(1,"Token không hợp lệ", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRE(1,"Email là bắt buộc", HttpStatus.BAD_REQUEST),
    CHAT_NOT_FOUND(1,"Không có lịch sử chat", HttpStatus.BAD_REQUEST),
    LOGIN_HISTORY_NOTFOUND(1, "Không tìm thấy phiên đăng nhập", HttpStatus.NOT_FOUND),
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
