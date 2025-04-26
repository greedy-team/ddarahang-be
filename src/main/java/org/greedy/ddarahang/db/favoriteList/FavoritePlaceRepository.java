package org.greedy.ddarahang.db.favoriteList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
    boolean existsByFavoriteListIdAndPlaceId(Long favoriteListId, Long placeId);

    int countByFavoriteListId(Long favoriteListId);

    Optional<FavoritePlace> findByFavoriteListIdAndPlaceId(Long favoriteListId, Long placeId);

    List<FavoritePlace> findAllByFavoriteListId(Long favoriteListId);

    List<FavoritePlace> findAllByFavoriteListIdIn(List<Long> favoriteListIds);

}
