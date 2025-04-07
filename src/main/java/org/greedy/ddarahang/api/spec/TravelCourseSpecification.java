package org.greedy.ddarahang.api.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.greedy.ddarahang.api.dto.TravelCourseListRequest;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Travel Course", description = "여행 코스 조회 API")
public interface TravelCourseSpecification {

    @Operation(summary = "여행 코스 리스트 조회", description = "필터, 국가, 지역 정보를 기반으로 여행 코스 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터 누락 또는 잘못된 값")
    })
    public ResponseEntity<Page<TravelCourseListResponse>> getTravelCourses(
            @Valid @ModelAttribute TravelCourseListRequest request
    );

    @Operation(summary = "여행 코스 상세 조회", description = "ID를 통해 여행 코스 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 여행 코스를 찾을 수 없음")
    })
    @Parameter(name = "id", description = "여행 코스 ID", example = "1")
    public ResponseEntity<TravelCourseResponse> getTravelCourseDetail(@PathVariable Long id);
}
