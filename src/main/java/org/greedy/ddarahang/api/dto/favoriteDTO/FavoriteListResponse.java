package org.greedy.ddarahang.api.dto.favoriteDTO;

import org.greedy.ddarahang.db.favoriteList.FavoriteList;

import java.util.List;

public record CreateFavoriteListResponse(
        Long id,
        String listName,
        List<FavoritePlaceResponse> places
) {
    public static CreateFavoriteListResponse from(FavoriteList favoriteList) {
        return new CreateFavoriteListResponse(
                favoriteList.getId(),
                favoriteList.getListName(),
                favoriteList.getPlaces().stream()
                        .map(FavoritePlaceResponse::from)
                        .toList()
        );
    }
}
