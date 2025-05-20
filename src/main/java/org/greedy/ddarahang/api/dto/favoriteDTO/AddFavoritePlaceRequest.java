package org.greedy.ddarahang.api.dto.favoriteDTO;

import jakarta.validation.constraints.NotNull;

public record AddFavoritePlaceRequest(

        @NotNull(message = "찜 목록 Id 값은 필수입니다.")
        Long favoriteListId,

        @NotNull(message = "장소 Id 값은 필수입니다.")
        Long placeId

) {
}
