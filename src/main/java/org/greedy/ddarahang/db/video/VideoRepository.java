package org.greedy.ddarahang.db.video;

import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<TravelCourse, Long> {
}
