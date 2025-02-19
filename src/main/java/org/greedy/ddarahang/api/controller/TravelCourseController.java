package org.greedy.ddarahang.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.dto.TravelCourseRequest;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travelcourses")
public class TravelCourseController {

    private final TravelCourseService travelCourseService;

    @GetMapping
    public List<TravelCourseResponse> getTravelCourses(@RequestBody @Valid TravelCourseRequest travelCourseRequest) {
        return travelCourseService.getTravelCourses(travelCourseRequest);
    }
}
