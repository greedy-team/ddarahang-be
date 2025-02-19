package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseRequest;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelCourseService {

    private final TravelCourseRepository travelCourseRepository;

    public List<TravelCourseResponse> getTravelCourses(TravelCourseRequest travelCourseRequest) {
        if(travelCourseRequest.regionName() == null) {
            List<TravelCourse> travelCourse = travelCourseRepository.findByCountryName(travelCourseRequest.countryName());

            return travelCourse.stream()
                    .map(TravelCourseResponse::from).toList();
        }

        List<TravelCourse> travelCourse = travelCourseRepository.findByRegionName(travelCourseRequest.regionName());

        return travelCourse.stream()
                .map(TravelCourseResponse::from).toList();
    }
}
