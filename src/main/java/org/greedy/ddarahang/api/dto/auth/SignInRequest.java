package org.greedy.ddarahang.api.dto.auth;

public record SignInRequest (
        String nickname,
        String password
){
}
