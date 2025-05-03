package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.service.AuthService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
}
