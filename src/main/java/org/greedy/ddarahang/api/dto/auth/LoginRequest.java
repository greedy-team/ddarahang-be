package org.greedy.ddarahang.api.dto.auth;

public record LoginRequest(
        String nickname,
        String password
){
}
