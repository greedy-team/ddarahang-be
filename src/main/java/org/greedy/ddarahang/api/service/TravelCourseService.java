package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseDetailResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.exception.InvalidCountryNameException;
import org.greedy.ddarahang.common.exception.MissingIdException;
import org.greedy.ddarahang.common.exception.NotFoundTravelCourseDetailException;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelCourseService {

    private final TravelCourseRepository travelCourseRepository;
    private final TravelCourseDetailRepository travelCourseDetailRepository;

    public List<TravelCourseListResponse> getTravelCourses(String countryName, String regionName) {
        validateCountryName(countryName);

        if (regionName.isBlank()) {
            return travelCourseRepository.findAllByCountryName(countryName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }
        return travelCourseRepository.findAllByRegionName(regionName)
                .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                .toList();
    }

    public TravelCourseResponse getTravelCourseDetail(Long id) {
        validateId(id);

        TravelCourse travelCourse = travelCourseRepository.findById(id)
                .orElseThrow(() -> new NotFoundTravelCourseDetailException("Travel course not found"));

        List<TravelCourseDetailResponse> travelCourseDetails = travelCourseDetailRepository.findAllByTravelCourseId(id)
                .stream().map(TravelCourseDetailResponse::from).toList();

        return TravelCourseResponse.from(travelCourse.getVideo(), travelCourseDetails);
    }

    public List<TravelCourseListResponse> getSortedByUploadDate(String countryName, String regionName) {
        validateCountryName(countryName);

        if (regionName.isBlank()) {
            return travelCourseRepository.findAllByCountryNameOrderByVideoUploadDateDesc(countryName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }
        return travelCourseRepository.findAllByRegionNameOrderByVideoUploadDateDesc(regionName)
                .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                .toList();
    }

    public List<TravelCourseListResponse> getSortedByViewCount(String countryName, String regionName) {
        validateCountryName(countryName);

        if (regionName.isBlank()) {
            return travelCourseRepository.findAllByCountryNameOrderByVideo_ViewCountDesc(countryName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }
        return travelCourseRepository.findAllByRegionNameOrderByVideo_ViewCountDesc(regionName)
                .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                .toList();
    }

    private void validateCountryName(String countryName) {
        if (countryName == null || countryName.isBlank()) {
            throw new InvalidCountryNameException("invalid country name");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new MissingIdException("invalid id");
        }
    }
}
