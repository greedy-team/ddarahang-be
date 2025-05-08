package org.greedy.ddarahang.api.service.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender emailSender;

    public void sendMail(String to, String title, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(title);
        helper.setText(content, true);
        helper.setReplyTo("ddarahang@gmail.com");
        try{
            emailSender.send(message);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to send email", e);
        }
    }
}
