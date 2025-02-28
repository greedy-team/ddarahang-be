package org.greedy.ddarahang.common.fixture;

import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetail;

public class TravelCourseDetailFixture {
    public static TravelCourseDetail getMockTravelCourseDetail(TravelCourse travelCourse, Place place) {
        return TravelCourseDetail.builder()
                .day(1)
                .orderInDay(1)
                .travelCourse(travelCourse)
                .place(place)
                .build();
    }
}
