package com.s2tv.sportshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP xác thực");
        message.setText("Mã OTP của bạn là: " + otp + "\nOTP có hiệu lực trong 5 phút.");
        message.setFrom("22521688@gm.uit.edu.vn");

        mailSender.send(message);
    }

    public void sendOrderConfirmationEmail(String to, Long orderCode, double totalAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Xác nhận đơn hàng #" + orderCode);
        message.setText("""
            Cảm ơn bạn đã đặt hàng tại WTM Sport!

            Mã đơn hàng: %s
            Tổng tiền: %.0f VND

            Đơn hàng của bạn đang được xử lý và sẽ sớm được xác nhận.

            Trân trọng,
            Đội ngũ WTM Sport
            """.formatted(orderCode, totalAmount));
        message.setFrom("22521688@gm.uit.edu.vn");

        mailSender.send(message);
    }

    public void sendOrderStatusUpdateEmail(String to, Long orderCode, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Cập nhật trạng thái đơn hàng #" + orderCode);
        message.setText("""
            Đơn hàng #%s của bạn đã được cập nhật trạng thái:

            👉 %s

            Cảm ơn bạn đã mua hàng tại WTM Sport!
            """.formatted(orderCode, status));
        message.setFrom("22521688@gm.uit.edu.vn");

        mailSender.send(message);
    }
}
