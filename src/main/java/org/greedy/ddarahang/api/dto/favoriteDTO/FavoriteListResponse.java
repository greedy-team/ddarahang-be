package org.greedy.ddarahang.api.dto.favoriteDTO;

import org.greedy.ddarahang.db.favoriteList.FavoriteList;

import java.util.List;

public record FavoriteListResponse(
        Long id,
        String listName,
        String description,
        List<FavoritePlaceResponse> places
) {
    public static FavoriteListResponse from(FavoriteList favoriteList) {
        return new FavoriteListResponse(
                favoriteList.getId(),
                favoriteList.getListName(),
                favoriteList.getDescription(),
                favoriteList.getFavoriteListPlaces().stream()
                        .map(favoriteListPlace -> FavoritePlaceResponse.from(favoriteListPlace.getPlace(), favoriteListPlace.getOrderInList()))
                        .toList()
        );
    }
}
