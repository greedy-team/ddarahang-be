package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.TravelCourseDetailResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.exception.InvalidCountryNameException;
import org.greedy.ddarahang.common.exception.InvalidFilterException;
import org.greedy.ddarahang.common.exception.MissingIdException;
import org.greedy.ddarahang.common.exception.NotFoundTravelCourseDetailException;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelCourseService {

    private final TravelCourseRepository travelCourseRepository;
    private final TravelCourseDetailRepository travelCourseDetailRepository;

    public List<TravelCourseListResponse> getTravelCourses(String filter, String countryName, String regionName) {
        validateCountryName(countryName);
        log.info("여행 코스 목록 조회: filter: {}, countryName: {}, regionName: {}", filter, countryName, regionName);

        if (filter.equalsIgnoreCase("default")) {
            if (regionName.isBlank()) {
                return travelCourseRepository.findAllByCountryName(countryName)
                        .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                        .toList();
            }
            return travelCourseRepository.findAllByRegionName(regionName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }

        if (filter.equalsIgnoreCase("viewCount")) {
            if (regionName.isBlank()) {
                return travelCourseRepository.findAllByCountryNameOrderByVideoViewCountDesc(countryName)
                        .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                        .toList();
            }
            return travelCourseRepository.findAllByRegionNameOrderByVideoViewCountDesc(regionName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }

        if (filter.equalsIgnoreCase("uploadDate")) {
            if (regionName.isBlank()) {
                return travelCourseRepository.findAllByCountryNameOrderByVideoUploadDateDesc(countryName)
                        .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                        .toList();
            }
            return travelCourseRepository.findAllByRegionNameOrderByVideoUploadDateDesc(regionName)
                    .stream().map(travelCourse -> TravelCourseListResponse.from(travelCourse, travelCourse.getVideo()))
                    .toList();
        }

        log.error("유효하지 않은 필터값: filter: {}", filter);
        throw new InvalidFilterException("Invalid filter value : " + filter);
    }

    public TravelCourseResponse getTravelCourseDetail(Long id) {
        validateId(id);
        log.info("여행 목록 상세 조회: ID: {}", id);

        TravelCourse travelCourse = travelCourseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("해당 id를 가진 여행 상세 정보가 존재하지 않습니다. ID: {}", id);
                    return new NotFoundTravelCourseDetailException("travel course not found");
                });

        List<TravelCourseDetailResponse> travelCourseDetails = travelCourseDetailRepository.findAllByTravelCourseId(id)
                .stream().map(TravelCourseDetailResponse::from).toList();

        return TravelCourseResponse.from(travelCourse.getTravelDays(),travelCourse.getVideo(), travelCourseDetails);
    }

    private void validateCountryName(String countryName) {
        if (countryName == null || countryName.isBlank()) {
            log.error("유효하지 않은 지역 이름입니다. countryName: {}", countryName);
            throw new InvalidCountryNameException("invalid country name");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            log.error("유효하지 않은 Id값 입니다. id: null");
            throw new MissingIdException("invalid id");
        }
    }
}
