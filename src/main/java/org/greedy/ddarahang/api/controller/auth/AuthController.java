package org.greedy.ddarahang.api.controller.auth;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.auth.LoginRequest;
import org.greedy.ddarahang.api.dto.auth.SignUpRequest;
import org.greedy.ddarahang.api.dto.auth.TokenResponse;
import org.greedy.ddarahang.api.service.auth.AuthService;
import org.greedy.ddarahang.api.service.auth.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService mailService;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signUp(@RequestBody SignUpRequest request) {
        if (authService.findByEmail(request.email()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/email/send") //메일 전송
    public ResponseEntity<Void> sendEmail(@RequestParam("email") String email) {
        mailService.sendCodeToEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify") //메일 인증
    public ResponseEntity<String> verifyEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {

        boolean isVerified = mailService.verifyCode(email, code);
        if (isVerified) {
            return ResponseEntity.ok("이메일 인증 성공!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못되었거나 만료된 인증 코드입니다.");
        }
    }
}
