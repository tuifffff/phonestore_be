package com.example.demo.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Tự tạo constructor để inject JavaMailSender
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Tự thêm private final
public class EmailService {

    JavaMailSender mailSender;

    /**
     * Gửi email đơn giản (Text-only)
     * Thường dùng để gửi mã OTP hoặc thông báo đặt hàng thành công
     */
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        // Ông có thể thêm dòng này để nhìn chuyên nghiệp hơn (email người gửi)
        // message.setFrom("phonehub.support@gmail.com");

        mailSender.send(message);
    }
}