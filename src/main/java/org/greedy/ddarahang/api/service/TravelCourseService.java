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
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.region.RegionRepository;
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
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;

    public Page<TravelCourseListResponse> getTravelCourses(TravelCourseListRequest request) {

        Long countryId = null;
        Long regionId = null;

        if (request.regionName() != null && !request.regionName().isBlank()) {
            regionId = regionRepository.findIdByName(request.regionName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid region name"));
        } else {
            countryId = countryRepository.findIdByName(request.countryName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid country name"));
        }

        TravelCourseListIdRequest idRequest = new TravelCourseListIdRequest( // 새로운 dto에 초기화
                countryId,
                regionId,
                request.pageNumber(),
                request.sortField()
        );

        Sort sort = Sort.by(Sort.Direction.DESC, request.sortField());
        Pageable pageable = PageRequest.of(idRequest.pageNumber(), PAGE_SIZE, sort);

        Page<TravelCourse> travelCourses;
        if (idRequest.regionId() == null) {
            travelCourses = travelCourseRepository.findTravelCoursesByCountryId(idRequest.countryId(), pageable);
        } else {
            travelCourses = travelCourseRepository.findByRegionIdAndCountryId(idRequest.regionId(), idRequest.countryId(), pageable);
        }

        log.info("여행 목록 정렬 성공: {}", idRequest.sortField());
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
