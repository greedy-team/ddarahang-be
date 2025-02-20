package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseDetailResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.common.exception.NotFoundTravelCourseDetailException;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelCourseService {

    private final TravelCourseRepository travelCourseRepository;
    private final TravelCourseDetailRepository travelCourseDetailRepository;

    public List<TravelCourseListResponse> getTravelCourses(String countryName, String regionName) {
        if (regionName.isBlank()) {
            List<TravelCourse> travelCourse = travelCourseRepository.findByCountryName(countryName);

            return travelCourse.stream()
                    .map(TravelCourseListResponse::from).toList();
        }

        List<TravelCourse> travelCourse = travelCourseRepository.findByRegionName(regionName);

        return travelCourse.stream()
                .map(TravelCourseListResponse::from).toList();
    }

    public TravelCourseResponse getTravelCourseDetail(Long id) {
        TravelCourse travelCourse = travelCourseRepository.findById(id)
                .orElseThrow(() -> new NotFoundTravelCourseDetailException("Travel course not found"));

        List<TravelCourseDetailResponse> travelCourseDetails = travelCourseDetailRepository.findAllByTravelCourseId(id)
                .stream().map(TravelCourseDetailResponse::from).toList();

        return TravelCourseResponse.from(travelCourse, travelCourseDetails);
    }
}
