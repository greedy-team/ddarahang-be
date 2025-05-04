package org.greedy.ddarahang.api.dto.auth;

import lombok.Builder;

@Builder
public record TokenResponse (
        String accessToken,
        String refreshToken
){
}
