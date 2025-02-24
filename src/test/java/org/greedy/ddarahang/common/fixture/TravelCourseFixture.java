package org.greedy.ddarahang.common.fixture;

import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.video.Video;

public class TravelCourseFixture {
    public static TravelCourse getMockTravelCourse(Video video, Country country, Region region) {
        return TravelCourse.builder()
                .travelDays(3)
                .video(video)
                .country(country)
                .region(region)
                .build();
    }
}
