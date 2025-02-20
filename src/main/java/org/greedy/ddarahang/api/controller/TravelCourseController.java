package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<TravelCourseResponse> getTravelCourses(@RequestParam String countryName, @RequestParam String regionName) {
        return travelCourseService.getTravelCourses(countryName, regionName);
    }
}
