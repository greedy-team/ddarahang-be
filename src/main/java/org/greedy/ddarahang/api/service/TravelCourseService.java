package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseDetailResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.common.exception.NotFoundTravelCourseDetailException;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.greedy.ddarahang.db.video.Video;
import org.greedy.ddarahang.db.video.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelCourseService {

    private final TravelCourseRepository travelCourseRepository;
    private final TravelCourseDetailRepository travelCourseDetailRepository;
    private final VideoRepository videoRepository;

    public List<TravelCourseListResponse> getTravelCourses(String countryName, String regionName) {
        if (regionName.isBlank()) {
            return travelCourseRepository.findByCountryName(countryName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }
        return travelCourseRepository.findByCountryName(countryName)
                .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                .toList();
    }

    public TravelCourseResponse getTravelCourseDetail(Long id) {
        TravelCourse travelCourse = travelCourseRepository.findById(id)
                .orElseThrow(() -> new NotFoundTravelCourseDetailException("Travel course not found"));

        List<TravelCourseDetailResponse> travelCourseDetails = travelCourseDetailRepository.findAllByTravelCourseId(id)
                .stream().map(TravelCourseDetailResponse::from).toList();

        return TravelCourseResponse.from(travelCourse.getVideo(), travelCourseDetails);
    }
}
