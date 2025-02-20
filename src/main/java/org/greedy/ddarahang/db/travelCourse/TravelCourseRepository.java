package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    List<TravelCourse> findAllByCountryName(String countryName);
    List<TravelCourse> findAllByRegionName(String regionName);
    List<TravelCourse> findAllByCountryNameOrderByUploadDateDesc(String countryName);
    List<TravelCourse> findAllByRegionNameOrderByUploadDateDesc(String regionName);
    List<TravelCourse> findAllByCountryNameOrderByViewCountDesc(String countryName);
    List<TravelCourse> findAllByRegionNameOrderByViewCountDesc(String regionName);

}
