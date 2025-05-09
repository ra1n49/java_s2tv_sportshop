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
    List<String> discount_ids;  // danh sách các ID giảm giá
    int delivery_fee;    // Phí vận chuyển

    ShippingAddress shipping_address;    // Địa chỉ giao hàng

    List<ProductOrder> products;    // Danh sách sản phẩm
    String order_status; // Trạng thái đơn hàng
    Date order_date;  // Ngày đặt hàng
    Date order_delivery_date; // Ngày giao hàng thực tế
    Date estimated_delivery_date;   // Ngày giao hàng dự kiến
    PaymentMethod payment_method;    // Phương thức thanh toán

    double order_total_price;      // Tổng tiền hàng
    double order_final_price;      // Tổng tiền phải trả (sau giảm giá + phí ship)
    double order_total_discount;   // Tổng giảm giá

    String order_note;   // Ghi chú đơn hàng
    boolean is_feedback; // Đánh giá hay chưa

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
