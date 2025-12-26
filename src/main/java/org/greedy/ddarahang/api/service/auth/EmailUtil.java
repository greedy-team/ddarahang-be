package org.greedy.ddarahang.api.service.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender emailSender;

    public void sendMail(String to, String title, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            helper.setReplyTo("ddarahang@gmail.com");

            emailSender.send(message);
            log.info("✅ 이메일 발송 성공 - 대상: {}", to);
        } catch (RuntimeException e) {
            log.error("❌ 이메일 발송 실패 - 대상: {}", to, e);
            throw new RuntimeException("Unable to send email", e);
        }
    }
}
