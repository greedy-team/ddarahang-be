package org.greedy.ddarahang.api.dto;

public record SignUpRequest (
        String nickname,
        String email,
        String password
){
}
