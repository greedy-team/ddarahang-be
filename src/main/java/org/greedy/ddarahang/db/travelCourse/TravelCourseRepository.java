package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @VideoEntityGraph
    Optional<TravelCourse> findById(Long id);

    @VideoEntityGraph
    List<TravelCourse> findAllByCountryName(String countryName);

    @VideoEntityGraph
    List<TravelCourse> findAllByRegionName(String regionName);

    @VideoEntityGraph
    List<TravelCourse> findAllByCountryNameOrderByVideoUploadDateDesc(String countryName);

    @VideoEntityGraph
    List<TravelCourse> findAllByRegionNameOrderByVideoUploadDateDesc(String regionName);

    @VideoEntityGraph
    List<TravelCourse> findAllByCountryNameOrderByVideoViewCountDesc(String countryName);

    @VideoEntityGraph
    List<TravelCourse> findAllByRegionNameOrderByVideoViewCountDesc(String regionName);

}
