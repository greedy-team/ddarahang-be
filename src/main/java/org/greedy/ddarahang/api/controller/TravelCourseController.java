package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travelcourses")
public class TravelCourseController {

    private final TravelCourseService travelCourseService;

    @GetMapping
    public ResponseEntity<List<TravelCourseListResponse>> getTravelCourses(
            @RequestParam String filter,
            @RequestParam String countryName,
            @RequestParam String regionName
    ) {
        log.info("GET /api/v1/travelcourses - filter: {}, countryName: {}, regionName: {}", filter, countryName, regionName);
        return ResponseEntity.ok(travelCourseService.getTravelCourses(filter, countryName, regionName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelCourseResponse> getTravelCourseDetail(@PathVariable Long id) {
        log.info("GET /api/v1/travelcourses/{} - Fetching travel course detail", id);
        return ResponseEntity.ok(travelCourseService.getTravelCourseDetail(id));
    }
}
