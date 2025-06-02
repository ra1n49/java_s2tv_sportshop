package com.s2tv.sportshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s2tv.sportshop.dto.request.CreatePaymentRequest;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.model.Order;
import com.s2tv.sportshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PayOS payOS;
    private final OrderRepository orderRepository;

    @Value("${app.domain}")
    private String domain;

    public CheckoutResponseData createPayment(CreatePaymentRequest request) {
        List<ItemData> itemList = request.getProducts().stream()
                .map(p ->
                        ItemData.builder()
                                .name(p.getName())
                                .quantity(p.getQuantity())
                                .price(p.getPrice())
                                .build()
                ).collect(Collectors.toList());

        PaymentData paymentData = PaymentData.builder()
                .orderCode(request.getOrderCode())
                .amount(request.getAmount())
                .description(request.getDescription())
                .items(itemList)
                .returnUrl(domain + "/orders/order-details/" + request.getOrderId())
                .cancelUrl(domain + "/checkout")
                .build();

        CheckoutResponseData data;
        try {
            data = payOS.createPaymentLink(paymentData);
        } catch (Exception e) {
            throw new AppException(ErrorCode.CREATE_PAYMENT_FAILED);
        }

//        Order order = orderRepository.findById(request.getOrderId())
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Order order = orderRepository.findByOrderCode(paymentData.getOrderCode())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setCheckoutUrl(data.getCheckoutUrl());
        orderRepository.save(order);

        return data;
    }

    public void handleWebhook(Map<String, Object> body) {
        ObjectMapper mapper = new ObjectMapper();

        String code = (String) body.get("code");
        String desc = (String) body.get("desc");
        String signature = (String) body.get("signature");

        WebhookData parsedData = mapper.convertValue(body.get("data"), WebhookData.class);
        Webhook webhook = Webhook.builder()
                .success(true)
                .code(code)
                .desc(desc)
                .signature(signature)
                .data(parsedData)
                .build();

        WebhookData webhookData = null;
        try {
            webhookData = payOS.verifyPaymentWebhookData(webhook);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_WEBHOOK_SIGNATURE);
        }

        Long orderCode = webhookData.getOrderCode();
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPaid(true);
        orderRepository.save(order);
    }

    public PaymentLinkData getInfoOfPayment(Long orderCode) {
        PaymentLinkData data;
        try {
            data = payOS.getPaymentLinkInformation(orderCode);
        } catch (Exception e) {
            throw new AppException(ErrorCode.CREATE_PAYMENT_FAILED);
        }

        return data;
    }

    public PaymentLinkData deletePayment(Long orderCode) {
        PaymentLinkData data;
        try {
            data = payOS.cancelPaymentLink(orderCode, "Người dùng không thực hiện thanh toán hoặc hủy");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    public boolean checkPaymentIsCancel(Long orderCode) {
        try {
            PaymentLinkData data = payOS.getPaymentLinkInformation(orderCode);
            return "CANCELLED".equals(data.getStatus());
        } catch (Exception e) {
            return false;
        }
    }
}