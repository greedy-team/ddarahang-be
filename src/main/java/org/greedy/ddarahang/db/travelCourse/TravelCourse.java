package org.greedy.ddarahang.db.travelCourse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.video.Video;

@Entity
@Getter
@Table(name = "travel_courses")
public class TravelCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_course_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "travel_days", nullable = false)
    private Integer travelDays;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

}
