package org.greedy.ddarahang.db.travelCourseDetail;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "travel_course_details")
public class TravelCourseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "`day`", nullable = false)
    private Integer day;

    @Column(nullable = false)
    private Integer orderInDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_course_id", nullable = false)
    private TravelCourse travelCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

}
