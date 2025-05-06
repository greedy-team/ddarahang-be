package org.greedy.ddarahang.api.dto.favoriteDTO;

import org.greedy.ddarahang.db.favoriteList.FavoritePlace;
import org.greedy.ddarahang.db.place.Place;

public record FavoritePlaceResponse(
        Long placeId,
        String placeName,
        String address,
        String tag,
        Double latitude,
        Double longitude,
        Integer orderInList
) {
    public static FavoritePlaceResponse from(Place place, Integer orderInList) {
        return new FavoritePlaceResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getTag(),
                place.getLatitude(),
                place.getLongitude(),
                orderInList
        );
    }

    public static FavoritePlaceResponse from(FavoritePlace favoritePlace) {
        return from(favoritePlace.getPlace(), favoritePlace.getOrderInList());
    }
}
