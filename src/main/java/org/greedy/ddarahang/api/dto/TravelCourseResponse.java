package org.greedy.ddarahang.api.dto;

import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.video.Video;

import java.time.LocalDate;
import java.util.List;

public record TravelCourseResponse(
        String countryName,
        String regionName,
        int travelDays,
        String creator,
        String title,
        String videoUrl,
        Long viewCount,
        LocalDate uploadDate,
        List<TravelCourseDetailResponse> details
) {
    public static TravelCourseResponse from(
            TravelCourse course,
            Video video,
            List<TravelCourseDetailResponse> details
    ) {
        return new TravelCourseResponse(
                course.getCountry().getName(),
                course.getRegion().getName(),
                course.getTravelDays(),
                video.getCreator(),
                video.getTitle(),
                video.getVideoUrl(),
                video.getViewCount(),
                video.getUploadDate(),
                details
        );
    }
}
