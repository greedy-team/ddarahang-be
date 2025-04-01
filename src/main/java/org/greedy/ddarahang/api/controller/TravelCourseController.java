package org.greedy.ddarahang.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Travel Course", description = "여행 코스 조회 API")
public class TravelCourseController {

    private final TravelCourseService travelCourseService;

    @GetMapping
    @Operation(summary = "여행 코스 리스트 조회", description = "필터, 국가, 지역 정보를 기반으로 여행 코스 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락 또는 잘못된 값")
    })
    @Parameters({
            @Parameter(name = "filter", description = "정렬 필터: viewCount | uploadDate", example = "viewCount"),
            @Parameter(name = "countryName", description = "국가명", example = "대한민국"),
            @Parameter(name = "regionName", description = "지역명 (빈 문자열 허용)", example = "서울")
    })
    public ResponseEntity<List<TravelCourseListResponse>> getTravelCourses(
            @RequestParam String filter,
            @RequestParam String countryName,
            @RequestParam String regionName
    ) {
        log.info("GET /travelcourses - filter: {}, countryName: {}, regionName: {}", filter, countryName, regionName);
        return ResponseEntity.ok(travelCourseService.getTravelCourses(filter, countryName, regionName));
    }

    @GetMapping("/{id}")
    @Operation(summary = "여행 코스 상세 조회", description = "ID를 통해 여행 코스 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 여행 코스를 찾을 수 없음")
    })
    @Parameter(name = "id", description = "여행 코스 ID", example = "1")
    public ResponseEntity<TravelCourseResponse> getTravelCourseDetail(@PathVariable Long id) {
        log.info("GET /travelcourses/{}", id);
        return ResponseEntity.ok(travelCourseService.getTravelCourseDetail(id));
    }
}
