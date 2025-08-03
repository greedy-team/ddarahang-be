package org.greedy.ddarahang.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.TravelCourseListIdRequest;
import org.greedy.ddarahang.api.dto.TravelCourseListRequest;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.region.RegionRepository;
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
public class TravelCourseController {

    private final TravelCourseService travelCourseService;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;

    @GetMapping("/api/v1/travelcourses")
    public ResponseEntity<Page<TravelCourseListResponse>> getTravelCourses( @Valid @ModelAttribute TravelCourseListRequest request) {
        log.info("GET /travelcourses - sortField: {}, countryName: {}, regionName: {}, pageNumber: {}",
                request.sortField(),
                request.countryName(),
                request.regionName(),
                request.pageNumber());

        Long countryId = null;
        Long regionId = null;

        if (request.regionName() != null && !request.regionName().isBlank()) {
            regionId = regionRepository.findIdByName(request.regionName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid region name"));
        }
        else {
            countryId = countryRepository.findIdByName(request.countryName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid country name"));
        }

        TravelCourseListIdRequest travelCourseListIdRequest = new TravelCourseListIdRequest(
                countryId,
                regionId,
                request.pageNumber(),
                request.sortField()
        );

        return ResponseEntity.ok(travelCourseService.getTravelCourses(travelCourseListIdRequest));
    }

    @GetMapping("/api/v1/travelcourses/{id}")
    public ResponseEntity<TravelCourseResponse> getTravelCourseDetail(@PathVariable Long id) {
        log.info("GET /travelcourses/{}", id);
        return ResponseEntity.ok(travelCourseService.getTravelCourseDetail(id));
    }
}
