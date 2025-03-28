package org.greedy.ddarahang.api.dto;

import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.video.Video;

import java.time.LocalDate;

public record TravelCourseListResponse(
        Long travelCourseId,
        String creator,
        String title,
        String thumbnailUrl,
        Long viewCount,
        LocalDate uploadDate
) {
    public static TravelCourseListResponse from(
            TravelCourse travelCourse,
            Video video
    ) {
        return new TravelCourseListResponse(
                travelCourse.getId(),
                video.getCreator(),
                video.getTitle(),
                video.getThumbnailUrl(),
                video.getViewCount(),
                video.getUploadDate()
        );
    }
}
