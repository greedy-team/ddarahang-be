package org.greedy.ddarahang.api.dto;

import java.time.LocalDate;
import java.util.List;
import org.greedy.ddarahang.db.video.Video;

public record TravelCourseResponse(
        String creator,
        String title,
        String videoUrl,
        Long viewCount,
        LocalDate uploadDate,
        List<TravelCourseDetailResponse> details
){
    public static TravelCourseResponse from(Video video, List<TravelCourseDetailResponse> details) {
        return new TravelCourseResponse(
                video.getCreator(),
                video.getTitle(),
                video.getVideoUrl(),
                video.getViewCount(),
                video.getUploadDate(),
                details
        );
    }
}
