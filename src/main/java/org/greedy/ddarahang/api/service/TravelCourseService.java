package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelCourseService {

    private final TravelCourseRepository travelCourseRepository;

    public List<TravelCourseResponse> getTravelCourses(String countryName, String regionName) {
        if (regionName.isBlank()) {
            List<TravelCourse> travelCourse = travelCourseRepository.findByCountryName(countryName);

            return travelCourse.stream()
                    .map(TravelCourseResponse::from).toList();
        }

        List<TravelCourse> travelCourse = travelCourseRepository.findByRegionName(regionName);

        return travelCourse.stream()
                .map(TravelCourseResponse::from).toList();
    }
}
