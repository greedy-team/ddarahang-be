package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @EntityGraph(attributePaths = {"video", "country", "region", })
    Optional<TravelCourse> findById(Long id);

    @Query("SELECT tc FROM TravelCourse tc " +
            "JOIN FETCH tc.video v " +
            "JOIN FETCH tc.country c " +
            "LEFT JOIN FETCH tc.region r " +
            "WHERE tc.country.id = :countryId")
    Page<TravelCourse> findTravelCoursesByCountryId(Long countryId, Pageable pageable);

    @Query("SELECT tc FROM TravelCourse tc " +
            "JOIN FETCH tc.video v " +
            "JOIN FETCH tc.region r " +
            "WHERE tc.region.id = :regionId")
    Page<TravelCourse> findByRegionIdAndCountryId(@Param("regionId") Long regionId,
                                                  @Param("countryId") Long countryId,
                                                  Pageable pageable);

}
