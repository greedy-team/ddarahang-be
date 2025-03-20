package org.greedy.ddarahang.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.api.service.TravelDataSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class GoogleSheetController {

    private final TravelDataSyncService travelDataSyncService;

    @PostMapping
    public ResponseEntity<Void> syncGoogleSheetWithDB() throws GeneralSecurityException, IOException {
        travelDataSyncService.syncGoogleSheetWithDB();
        return ResponseEntity.ok().build();
    }
}
