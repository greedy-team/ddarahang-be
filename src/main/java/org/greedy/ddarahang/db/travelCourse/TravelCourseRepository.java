package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    List<TravelCourse> findByCountryName(String countryName);
    List<TravelCourse> findByRegionName(String regionName);
}
