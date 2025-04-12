package org.greedy.ddarahang.db.favoriteList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteListPlaceRepository extends JpaRepository<FavoriteListPlace, Long> {
    boolean existsByFavoriteListIdAndPlaceId(Long favoriteListId, Long placeId);

    int countByFavoriteListId(Long favoriteListId);

    Optional<FavoriteListPlace> findByFavoriteListIdAndPlaceId(Long favoriteListId, Long placeId);

    List<FavoriteListPlace> findAllByFavoriteListId(Long favoriteListId);

    List<FavoriteListPlace> findAllByFavoriteListIdIn(List<Long> favoriteListIds);

}
