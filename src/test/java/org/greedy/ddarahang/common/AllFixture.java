package org.greedy.ddarahang.common;

import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.LocationType;
import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetail;
import org.greedy.ddarahang.db.video.Video;

import java.time.LocalDate;

public class AllFixture {
    public static Country getMockCountry() {
        return Country.builder()
                .name("대한민국")
                .locationType(LocationType.DOMESTIC)
                .build();
    }

    public static Region getMockRegion(Country country) {
        return Region.builder()
                .name("서울")
                .country(country)
                .build();
    }

    public static Place getMockPlace(Region region) {
        return Place.builder()
                .name("경복궁")
                .address("서울 종로구 세종로 1-1")
                .region(region)
                .build();
    }

    public static Video getMockVideo(LocalDate now) {
        return Video.builder()
                .creator("여행유튜버A")
                .title("서울 여행 브이로그")
                .videoUrl("https://youtube.com/v1")
                .thumbnailUrl("https://img.youtube.com/1.jpg")
                .viewCount(10000L)
                .uploadDate(now)
                .build();
    }

    public static TravelCourse getMockTravelCourse(Video video, Country country, Region region) {
        return TravelCourse.builder()
                .travelDays(3)
                .video(video)
                .country(country)
                .region(region)
                .build();
    }

    public static TravelCourseDetail getMockTravelCourseDetail(TravelCourse travelCourse, Place place) {
        return TravelCourseDetail.builder()
                .day(1)
                .orderInDay(1)
                .travelCourse(travelCourse)
                .place(place)
                .build();
    }
}

