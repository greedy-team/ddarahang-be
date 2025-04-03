package org.greedy.ddarahang.api.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleSheetSpecification {

    @Operation(summary = "Google Sheet 동기화",
            description = "Google Sheet의 데이터를 DB와 동기화하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동기화 성공"),
            @ApiResponse(responseCode = "500", description = "Google API 또는 DB 오류 발생")
    })
    public ResponseEntity<Void> syncGoogleSheetWithDB() throws GeneralSecurityException, IOException;
}
