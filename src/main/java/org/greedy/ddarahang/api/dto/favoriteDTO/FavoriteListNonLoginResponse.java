package org.greedy.ddarahang.api.dto.favoriteDTO;

public record FavoriteListNonLoginResponse(
        Long orderInPlace,
        String placeName,
        String placeAddress,
        Double latitude,
        Double longitude
) {
}
