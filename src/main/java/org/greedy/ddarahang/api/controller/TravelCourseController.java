package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travelcourses")
public class TravelCourseController {

    private final TravelCourseService travelCourseService;

    @GetMapping
    public ResponseEntity<List<TravelCourseListResponse>> getTravelCourses(@RequestParam String countryName, @RequestParam String regionName) {
        return ResponseEntity.ok(travelCourseService.getTravelCourses(countryName, regionName));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TravelCourseResponse> getTravelCourseDetail(@PathVariable Long id) {
        return travelCourseService.getTravelCourseDetail(id);
    }
}
