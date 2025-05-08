package org.greedy.ddarahang.api.dto.auth;

public record LoginRequest(
        String email,
        String password
){
}
