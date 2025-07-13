package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @EntityGraph(attributePaths = {"video", "country", "region", })
    Optional<TravelCourse> findById(Long id);

    @EntityGraph(attributePaths = {"video", "country", "region"})
    Page<TravelCourse> findTravelCoursesByCountryName(String countryName, Pageable pageable);

    @EntityGraph(attributePaths = {"video", "country", "region"})
    Page<TravelCourse> findTravelCoursesByRegionName(String regionName, Pageable pageable);
}
