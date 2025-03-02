package org.greedy.ddarahang.api.dto;

import org.greedy.ddarahang.db.video.Video;

import java.time.LocalDate;
import java.util.List;

public record TravelCourseResponse(
        int travelDays,
        String creator,
        String title,
        String videoUrl,
        Long viewCount,
        LocalDate uploadDate,
        List<TravelCourseDetailResponse> details
) {
    public static TravelCourseResponse from(Video video, List<TravelCourseDetailResponse> details) {
        return new TravelCourseResponse(
                details.size(),
                video.getCreator(),
                video.getTitle(),
                video.getVideoUrl(),
                video.getViewCount(),
                video.getUploadDate(),
                details
        );
    }
}
