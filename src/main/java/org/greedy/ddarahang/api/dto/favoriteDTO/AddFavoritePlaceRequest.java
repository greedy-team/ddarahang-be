package org.greedy.ddarahang.api.dto.favoriteDTO;

public record AddFavoritePlaceRequest(
        Long favoriteListId,
        Long placeId
) {
}
