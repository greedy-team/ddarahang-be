package org.greedy.ddarahang.api.dto.auth;

public record SignUpRequest (
        String nickname,
        String email,
        String password
){
}
