package org.greedy.ddarahang.api.dto;

import org.greedy.ddarahang.db.travelCourse.TravelCourse;

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
            List<TravelCourseDetailResponse> details
    ) {
        return new TravelCourseResponse(
                course.getCountry().getName(),
                course.getRegion().getName(),
                course.getTravelDays(),
                course.getVideo().getCreator(),
                course.getVideo().getTitle(),
                course.getVideo().getVideoUrl(),
                course.getVideo().getViewCount(),
                course.getVideo().getUploadDate(),
                details
        );
    }
}
