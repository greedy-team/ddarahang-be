package org.greedy.ddarahang.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TravelCourseRequest(
        @NotBlank(message = "나라 이름이 누락되었습니다.")
        String countryName,
        String regionName
) {
}
