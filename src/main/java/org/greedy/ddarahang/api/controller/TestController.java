package org.greedy.ddarahang.api.controller;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final ServletWebServerApplicationContext webServerAppContext;
    private static final String RESPONSE_BODY_MESSAGE = "버전 1 응답값입니다. 무중단 배포 도입 전입니다.";

    public TestController(ServletWebServerApplicationContext webServerAppContext) {
        this.webServerAppContext = webServerAppContext;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> test() {
        WebServer webServer = webServerAppContext.getWebServer();
        int port = webServer.getPort();
        String response = RESPONSE_BODY_MESSAGE + "\n실행 중인 포트는 " + port + "입니다.";
        return ResponseEntity.ok(response);
    }
}