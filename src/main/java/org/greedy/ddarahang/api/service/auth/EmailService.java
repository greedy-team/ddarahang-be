package org.greedy.ddarahang.api.service.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.auth.VerificationCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailSender emailSender;

    private final Map<String, VerificationCode> codeMap = new ConcurrentHashMap<>();

    public void sendCodeToEmail(String email) { //이메일 전송
        String code = generateRandomCode(6);
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);

        codeMap.put(email, new VerificationCode(code, expireTime));

        String title = "[따라행(ddarahang)] 이메일 인증";
        String content = "<html>"
                + "<body>"
                + "<h2>따라행(ddarahang) 인증 코드</h2>"
                + "<h1>" + code + "</h1>"
                + "<p>인증코드는 10분 동안 유효합니다.</p>"
                + "</body>"
                + "</html>";
        try {
            emailSender.sendMail(email, title, content);
        } catch (MessagingException e){
            throw new RuntimeException("Unable to send email", e);
        }
    }

    public String generateRandomCode(int length){ //랜덤코드 생성
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkLmnopqrstuvwxyz0123456789";
        StringBuilder randomCode = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for(int i = 0; i < length; i++){
            int index = random.nextInt(characters.length());
            randomCode.append(characters.charAt(index));
        }
        return randomCode.toString();
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode stored = codeMap.get(email);

        if (stored == null || !stored.getCode().equals(code)) return false;
        if (stored.getExpiresTime().isBefore(LocalDateTime.now())) return false;
        codeMap.remove(email); // 인증 성공 시 삭제

        return true;
    }
}
