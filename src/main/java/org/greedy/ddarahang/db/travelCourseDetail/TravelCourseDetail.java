package org.greedy.ddarahang.db.travelCourseDetail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @Column(name = "travel_course_detail_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "day", nullable = false)
    private Integer day;

    @Column(name = "order_in_day", nullable = false)
    private Integer orderInDay;

    @ManyToOne
    @JoinColumn(name = "travel_course_id", nullable = false)
    private TravelCourse travelCourse;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

}
