package org.greedy.ddarahang.api.dto;

import lombok.Builder;

@Builder
public record TokenResponse (
        String accessToken,
        String refreshToken
){
}
