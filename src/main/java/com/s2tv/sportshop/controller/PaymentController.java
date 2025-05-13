package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.CreatePaymentRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentLinkData;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ApiResponse<CheckoutResponseData> createPayment(@RequestBody CreatePaymentRequest request) {
        return ApiResponse.<CheckoutResponseData>builder()
                .EC(0)
                .EM("Tạo thông tin thanh toán thành công")
                .result(paymentService.createPayment(request))
                .build();
    }

    @PostMapping("/payos-webhook")
    public ApiResponse<Void> handleWebhook(@RequestBody Map<String, Object> body) {
        paymentService.handleWebhook(body);
        return ApiResponse.<Void>builder()
            .EC(0)
            .EM("Xác nhận thanh toán thành công")
            .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/info-of-payment/{orderCode}")
    public ApiResponse<PaymentLinkData> getInfoOfPayment(@PathVariable("orderCode") Long orderCode) {
        return ApiResponse.<PaymentLinkData>builder()
                .EC(0)
                .EM("Lấy thông tin thanh toán thành công")
                .result(paymentService.getInfoOfPayment(orderCode))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderCode}")
    public ApiResponse<PaymentLinkData> deletePayment(@PathVariable("orderCode") Long orderCode) {
        return ApiResponse.<PaymentLinkData>builder()
                .EC(0)
                .EM("Xóa thông tin thanh toán thành công")
                .result(paymentService.deletePayment(orderCode))
                .build();
    }
}
