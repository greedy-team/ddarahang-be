package org.greedy.ddarahang.db.favoriteList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteListRepository extends JpaRepository<FavoriteList, Long> {
}
