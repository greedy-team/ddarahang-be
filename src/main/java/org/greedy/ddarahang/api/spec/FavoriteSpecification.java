package org.greedy.ddarahang.api.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.greedy.ddarahang.api.dto.favoriteDTO.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "찜 목록 및 찜 장소 관리", description = "찜 목록 생성, 조회, 삭제 및 찜 장소 추가/삭제 API")
public interface FavoriteSpecification {

    @Operation(summary = "찜 목록 생성",
            description = "새로운 찜 목록을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 목록 생성 성공")
    })
    ResponseEntity<Void> createFavoriteList(CreateFavoriteListRequest request);

    @Operation(summary = "찜 목록 전체 조회",
            description = "모든 찜 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 목록 전체 조회 성공")
    })
    ResponseEntity<List<FavoriteListResponse>> getFavoriteLists();

    @Operation(summary = "찜 목록 삭제", description = "지정된 ID의 찜 목록을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 목록 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "찜 목록을 찾을 수 없음")
    })
    ResponseEntity<DeleteFavoriteListResponse> deleteFavoriteList(Long favoriteListId);

    @Operation(summary = "찜 목록에 장소 추가", description = "찜 목록에 장소를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소 추가 성공"),
            @ApiResponse(responseCode = "404", description = "찜 목록 또는 장소를 찾을 수 없음")
    })
    ResponseEntity<FavoritePlaceResponse> addFavoritePlace(AddFavoritePlaceRequest request);

    @Operation(summary = "찜 목록에서 장소 삭제", description = "찜 목록에서 특정 장소를 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "찜 목록 또는 장소를 찾을 수 없음")
    })
    ResponseEntity<DeleteFavoritePlaceResponse> deleteFavoritePlace(Long favoriteListId, Long placeId);
}
