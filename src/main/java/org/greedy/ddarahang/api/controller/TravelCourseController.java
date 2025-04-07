package org.greedy.ddarahang.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.TravelCourseListRequest;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.greedy.ddarahang.api.spec.TravelCourseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travelcourses")
public class TravelCourseController implements TravelCourseSpecification {

    private final TravelCourseService travelCourseService;

    @GetMapping
    public ResponseEntity<Page<TravelCourseListResponse>> getTravelCourses(
            @Valid @ModelAttribute TravelCourseListRequest request
    ) {
        log.info("GET /travelcourses - sortField: {}, countryName: {}, regionName: {}, pageNumber: {}",
                request.sortField(), request.countryName(), request.regionName(), request.pageNumber());
        return ResponseEntity.ok(travelCourseService.getTravelCourses(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelCourseResponse> getTravelCourseDetail(@PathVariable Long id) {
        log.info("GET /travelcourses/{}", id);
        return ResponseEntity.ok(travelCourseService.getTravelCourseDetail(id));
    }
}
