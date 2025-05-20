package org.greedy.ddarahang.api.dto.favoriteDTO;

import org.greedy.ddarahang.db.favoriteList.FavoriteList;

public record FavoriteListResponse(
        Long id,
        String listName,
        String description
) {
    public static FavoriteListResponse from(FavoriteList favoriteList) {
        return new FavoriteListResponse(
                favoriteList.getId(),
                favoriteList.getListName(),
                favoriteList.getDescription()
        );
    }
}
