package org.greedy.ddarahang.api.dto;

import java.time.LocalDate;
import java.util.List;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;

public record TravelCourseResponse(
        String creator,
        String title,
        String videoUrl,
        Long viewCount,
        LocalDate uploadDate,
        List<TravelCourseDetailResponse> details
){
    public static TravelCourseResponse from(TravelCourse travelCourse, List<TravelCourseDetailResponse> details) {
        return new TravelCourseResponse(
                travelCourse.getVideo().getCreator(),
                travelCourse.getVideo().getTitle(),
                travelCourse.getVideo().getVideoUrl(),
                travelCourse.getVideo().getViewCount(),
                travelCourse.getVideo().getUploadDate(),
                details
        );
    }
}
