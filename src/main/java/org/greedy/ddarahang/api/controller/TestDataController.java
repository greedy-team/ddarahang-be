package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.service.TestDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestDataController {

    private final TestDataService testDataService;

    @PostMapping("/api/v1/test-data/generate-test1")
    public ResponseEntity<String> generateTestData() {
        log.info("Generating test data...");
        testDataService.generateTestData();
        return ResponseEntity.ok("Test data generation started. Check logs for progress.");
    }

    @PostMapping("/api/v1/test-data/generate-popular-courses")
    public ResponseEntity<String> generatePopularCoursesTestData() {
        log.info("Generating popular courses test data (for view_count sorting test)...");
        testDataService.generatePopularCoursesTestData(); // TestDataService의 generatePopularCoursesTestData 호출
        return ResponseEntity.ok("Popular courses test data generation started. Check logs for progress.");
    }

}