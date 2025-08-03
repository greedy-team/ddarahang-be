package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.TravelCourseDetailResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListIdRequest;
import org.greedy.ddarahang.api.dto.TravelCourseListRequest;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.exception.ErrorMessage;
import org.greedy.ddarahang.common.exception.NotFoundDataException;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelCourseService {

    private final static int PAGE_SIZE = 8;

    private final TravelCourseRepository travelCourseRepository;
    private final TravelCourseDetailRepository travelCourseDetailRepository;

    public Page<TravelCourseListResponse> getTravelCourses(TravelCourseListIdRequest request) {
        String sortKey = switch (request.sortField()) {
            case "uploadDate" -> "videoUploadDate";
            default -> "videoViewCount"; // 기본 정렬
        };
        Sort sort = Sort.by(Sort.Direction.DESC, sortKey);

        Pageable pageable = PageRequest.of(request.pageNumber(), PAGE_SIZE, sort);

        Page<TravelCourse> travelCourses;
        if (request.regionId() == null) {
            travelCourses = travelCourseRepository.findTravelCoursesByCountryId(request.countryId(), pageable);
        } else {
            travelCourses = travelCourseRepository.findByRegionIdAndCountryId(request.regionId(), request.countryId(), pageable);
        }

        log.info("여행 목록 정렬 성공: {}", request.sortField());
        return travelCourses.map(course -> TravelCourseListResponse.from(course, course.getVideo()));
    }

    public TravelCourseResponse getTravelCourseDetail(Long id) {
        TravelCourse travelCourse = travelCourseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("해당 id를 가진 여행 상세 정보가 존재하지 않습니다. ID: {}", id);
                    return new NotFoundDataException(ErrorMessage.FAILED_TO_INSERT_TRAVEL_COURSE_DETAILS);
                });

        List<TravelCourseDetailResponse> travelCourseDetails = travelCourseDetailRepository.findAllByTravelCourseId(id)
                .stream().map(TravelCourseDetailResponse::from).toList();

        return TravelCourseResponse.from(travelCourse, travelCourse.getVideo(), travelCourseDetails);
    }

}
