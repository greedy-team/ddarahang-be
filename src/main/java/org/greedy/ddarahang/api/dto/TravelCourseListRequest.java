package org.greedy.ddarahang.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record TravelCourseListRequest(
        @NotBlank(message = "국가명을 입력해 주세요.")
        String countryName,

        @NotNull
        String regionName,

        @NotNull
        @Min(value = 0, message = "페이지 번호에 음수는 입력될 수 없습니다.")
        @Max(value = Integer.MAX_VALUE, message = "페이지 번호의 최대값을 초과했습니다.")
        Integer pageNumber,

        @NotNull // 새로 추가된 pageSize 필드
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        Integer pageSize,

        @NotNull
        @Pattern(regexp = "^(viewCount|uploadDate)")
        String sortField
) {
}
