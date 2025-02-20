package org.greedy.ddarahang.db.video;

import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}
