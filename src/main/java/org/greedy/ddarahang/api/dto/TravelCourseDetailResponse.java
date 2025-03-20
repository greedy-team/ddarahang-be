package org.greedy.ddarahang.api.dto;

import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetail;

public record TravelCourseDetailResponse(
        int day,
        int orderInDay,
        String placeName,
        String placeAddress,
        String tag,
        Double lat,
        Double lng
) {
    public static TravelCourseDetailResponse from(TravelCourseDetail travelCourseDetail) {
        return new TravelCourseDetailResponse(
                travelCourseDetail.getDay(),
                travelCourseDetail.getOrderInDay(),
                travelCourseDetail.getPlace().getName(),
                travelCourseDetail.getPlace().getAddress(),
                travelCourseDetail.getPlace().getTag(),
                travelCourseDetail.getPlace().getLatitude(),
                travelCourseDetail.getPlace().getLongitude()
        );
    }
}
