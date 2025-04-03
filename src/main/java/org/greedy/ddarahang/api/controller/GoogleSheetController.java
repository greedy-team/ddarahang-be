package org.greedy.ddarahang.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.service.TravelDataSyncService;
import org.greedy.ddarahang.api.spec.GoogleSheetSpecification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Tag(name = "Google Sheet", description = "구글 시트 동기화 API")
public class GoogleSheetController implements GoogleSheetSpecification {

    private final TravelDataSyncService travelDataSyncService;

    @PostMapping
    public ResponseEntity<Void> syncGoogleSheetWithDB() throws GeneralSecurityException, IOException {
        travelDataSyncService.syncGoogleSheetWithDB();
        return ResponseEntity.ok().build();
    }
}
