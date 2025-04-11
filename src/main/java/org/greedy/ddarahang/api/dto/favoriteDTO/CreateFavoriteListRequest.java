package org.greedy.ddarahang.api.dto.favoriteDTO;

import jakarta.validation.constraints.NotNull;

public record CreateFavoriteListRequest(

        @NotNull(message = "찜 목록명은 필수입니다.")
        String listName,
        String description
) {
}
