package org.greedy.ddarahang.api.dto;

public record TravelCourseListIdRequest (
        Long countryId,
        Long regionId,
        Integer pageNumber,
        String sortField
){
}
