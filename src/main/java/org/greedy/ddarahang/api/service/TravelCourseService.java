package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.TravelCourseDetailResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListRequest;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.exception.MissingIdException;
import org.greedy.ddarahang.common.exception.NotFoundTravelCourseDetailException;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<TravelCourseListResponse> getTravelCourses(TravelCourseListRequest request) {
        Pageable pageable = PageRequest.of(request.pageNumber(), request.pageSize());

        Page<TravelCourse> travelCourses = travelCourseRepository.findAllSortedNative(
                request.countryName(),
                request.regionName(),
                request.sortField(),
                pageable
        );

        log.info("여행 목록 정렬 성공: {}", request.sortField());

        return travelCourses.map(travelCourse ->
                TravelCourseListResponse.from(travelCourse, travelCourse.getVideo())
        );
    }


    public TravelCourseResponse getTravelCourseDetail(Long id) {
        validateId(id);

        TravelCourse travelCourse = travelCourseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("해당 id를 가진 여행 상세 정보가 존재하지 않습니다. ID: {}", id);
                    return new NotFoundTravelCourseDetailException("travel course not found");
                });

        List<TravelCourseDetailResponse> travelCourseDetails = travelCourseDetailRepository.findAllByTravelCourseId(id)
                .stream().map(TravelCourseDetailResponse::from).toList();

        return TravelCourseResponse.from(travelCourse, travelCourseDetails);
    }

    private void validateId(Long id) {
        log.info("여행 목록 상세 조회: ID: {}", id);

        if (id == null) {
            log.error("유효하지 않은 Id값 입니다. id: null");
            throw new MissingIdException("invalid id");
        }
    }
}
