package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @VideoEntityGraph
    Optional<TravelCourse> findById(Long id);

    @VideoEntityGraph
    Page<TravelCourse> findTravelCoursesByCountryName(String countryName, Pageable pageable);

    @VideoEntityGraph
    Page<TravelCourse> findTravelCoursesByRegionName(String regionName, Pageable pageable);
}
