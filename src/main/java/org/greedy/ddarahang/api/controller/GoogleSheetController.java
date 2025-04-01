package org.greedy.ddarahang.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.service.TravelDataSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Tag(name = "Google Sheet", description = "구글 시트 동기화 API")
public class GoogleSheetController {

    private final TravelDataSyncService travelDataSyncService;

    @PostMapping
    @Operation(summary = "Google Sheet 동기화",
            description = "Google Sheet의 데이터를 DB와 동기화하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동기화 성공"),
            @ApiResponse(responseCode = "500", description = "Google API 또는 DB 오류 발생")
    })
    public ResponseEntity<Void> syncGoogleSheetWithDB() throws GeneralSecurityException, IOException {
        travelDataSyncService.syncGoogleSheetWithDB();
        return ResponseEntity.ok().build();
    }
}
