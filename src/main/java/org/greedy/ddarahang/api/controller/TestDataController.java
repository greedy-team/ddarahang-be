package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.service.TestDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestDataController {

    private final TestDataService testDataService;

    @PostMapping("/api/v1/test-data/generate-test1")
    public ResponseEntity<String> generateTestData() {
        log.info("Generating test data...");
        testDataService.generateTest1Data();
        return ResponseEntity.ok("Test data generation started. Check logs for progress.");
    }

    @PostMapping("/api/v1/test-data/generate-test2")
    public ResponseEntity<String> generatePopularCoursesTestData() {
        log.info("Generating popular courses test data (for view_count sorting test)...");
        testDataService.generateTest2SortData();
        return ResponseEntity.ok("Popular courses test data generation started. Check logs for progress.");
    }

    @PostMapping("/api/v1/test-data/generate-nplus1data")
    public ResponseEntity<String> generateNplus1TestData(@RequestParam(defaultValue = "1000000") int totalCourses) {
        log.info("Request received to generate N+1 test data (excluding TravelCourseDetail & Place data): TravelCourses/Videos = {}ea, Others = Small quantities.", totalCourses);
        if (totalCourses <= 0 || totalCourses > 5_000_000) {
            return ResponseEntity.badRequest().body("생성할 TravelCourse 및 Video 개수는 1개 이상 5백만 개 이하여야 합니다.");
        }
        testDataService.generateMinimalCoreTestDataForNplus1(totalCourses);
        return ResponseEntity.ok("N+1 테스트 데이터 생성이 시작되었습니다. 서버 로그를 확인해주세요.");
    }

    @PostMapping("/api/v1/test-data/clear-all") // 삭제로직
    public ResponseEntity<String> clearAllData() {
        log.warn("!!!! 모든 테스트 데이터를 삭제하는 요청이 접수되었습니다. 복구 불가능한 작업입니다. !!!!");
        testDataService.clearAllTestData();
        return ResponseEntity.ok("모든 테스트 데이터 삭제가 시작되었습니다. 서버 로그를 확인해주세요.");
    }

    @PostMapping("/api/v1/test-data/generate-test3")
    public ResponseEntity<String> generateRegionCountryFilterData() {
        log.info("Generating data for region+country filter performance test...");
        testDataService.generateRegionCountryFilterTestData();
        return ResponseEntity.ok("Region+Country filter test data generation started. Check logs for progress.");
    }

}