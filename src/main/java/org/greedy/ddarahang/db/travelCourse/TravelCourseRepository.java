package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @EntityGraph(attributePaths = {"video"})
    @NonNull
    Optional<TravelCourse> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"video"})
    List<TravelCourse> findAllByCountryName(String countryName);

    @EntityGraph(attributePaths = {"video"})
    List<TravelCourse> findAllByRegionName(String regionName);

    @EntityGraph(attributePaths = {"video"})
    List<TravelCourse> findAllByCountryNameOrderByVideoUploadDateDesc(String countryName);

    @EntityGraph(attributePaths = {"video"})
    List<TravelCourse> findAllByRegionNameOrderByVideoUploadDateDesc(String regionName);

    @EntityGraph(attributePaths = {"video"})
    List<TravelCourse> findAllByCountryNameOrderByVideoViewCountDesc(String countryName);

    @EntityGraph(attributePaths = {"video"})
    List<TravelCourse> findAllByRegionNameOrderByVideoViewCountDesc(String regionName);

}
