package org.greedy.ddarahang.db.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
