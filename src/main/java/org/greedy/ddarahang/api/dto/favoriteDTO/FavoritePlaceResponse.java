package org.greedy.ddarahang.api.dto.favoriteDTO;

import org.greedy.ddarahang.db.place.Place;

public record FavoritePlaceResponse(
        Long id,
        String placeName,
        String address,
        Double latitude,
        Double longitude
) {
    public static FavoritePlaceResponse from(Place place) {
        return new FavoritePlaceResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude()
        );
    }
}
