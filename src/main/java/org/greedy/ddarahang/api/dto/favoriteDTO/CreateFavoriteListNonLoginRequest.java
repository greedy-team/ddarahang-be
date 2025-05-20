package org.greedy.ddarahang.api.dto.favoriteDTO;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateFavoriteListNonLoginRequest(

        @NotNull(message = "placeIds는 null일 수 없습니다.")
        List<Long> placeIds
) {
}
