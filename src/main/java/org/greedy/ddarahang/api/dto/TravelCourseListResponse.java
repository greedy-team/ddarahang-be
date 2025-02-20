package org.greedy.ddarahang.api.dto;

import org.greedy.ddarahang.db.travelCourse.TravelCourse;

import java.time.LocalDate;

public record TravelCourseListResponse(
        Long travelCourseId,
        String creator,
        String title,
        String thumbnailUrl,
        Long viewCount,
        LocalDate uploadDate
) {
    public static TravelCourseListResponse from(TravelCourse travelCourse) {
        return new TravelCourseListResponse(
                travelCourse.getId(),
                travelCourse.getVideo().getCreator(),
                travelCourse.getVideo().getTitle(),
                travelCourse.getVideo().getThumbnailUrl(),
                travelCourse.getVideo().getViewCount(),
                travelCourse.getVideo().getUploadDate()
        );
    }
}
