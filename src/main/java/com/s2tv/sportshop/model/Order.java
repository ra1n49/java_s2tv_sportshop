package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "Order")
public class Order {
    @Id
    String id;
    List<String> discountIds;  // danh sách các ID giảm giá
    int deliveryFee;    // Phí vận chuyển

    ShippingAddress shippingAddress;    // Địa chỉ giao hàng

    List<ProductOrder> products;    // Danh sách sản phẩm
    String orderStatus; // Trạng thái đơn hàng
    Date orderDate;  // Ngày đặt hàng
    Date orderDeliveryDate; // Ngày giao hàng thực tế
    Date estimatedDeliveryDate;   // Ngày giao hàng dự kiến
    PaymentMethod paymentMethod;    // Phương thức thanh toán

    double orderTotalPrice;      // Tổng tiền hàng
    double orderFinalPrice;      // Tổng tiền phải trả (sau giảm giá + phí ship)
    double orderTotalDiscount;   // Tổng giảm giá

    String orderNote;   // Ghi chú đơn hàng
    boolean isFeedback; // Đánh giá hay chưa

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
