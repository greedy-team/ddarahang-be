package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    List<TravelCourse> findAllByCountryName(String countryName);
    List<TravelCourse> findAllByRegionName(String regionName);

    List<TravelCourse> findAllByCountryNameOrderByVideo_UploadDateDesc(String countryName);
    List<TravelCourse> findAllByRegionNameOrderByVideo_UploadDateDesc(String regionName);

    List<TravelCourse> findAllByCountryNameOrderByVideo_ViewCountDesc(String countryName);
    List<TravelCourse> findAllByRegionNameOrderByVideo_ViewCountDesc(String regionName);

}
