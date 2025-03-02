package org.greedy.ddarahang.db.travelCourseDetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelCourseDetailRepository extends JpaRepository<TravelCourseDetail, Long> {

    List<TravelCourseDetail> findAllByTravelCourseId(Long travelCourseId);
}
