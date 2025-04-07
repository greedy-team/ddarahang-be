package org.greedy.ddarahang.api.dto.favoriteDTO;

public record AddFavoritePlace(
        Long favoriteListId,
        String placeName,
        String address,
        Double latitude,
        Double longitude
) {
}
