package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.SignInRequest;
import org.greedy.ddarahang.api.dto.SignUpRequest;
import org.greedy.ddarahang.api.dto.TokenResponse;
import org.greedy.ddarahang.api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<TokenResponse> signUp(@RequestBody SignUpRequest request) {
        if (authService.findByNickname(request.nickname()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @PostMapping("/signIn")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signIn(request));
    }
}
