package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.CreateFavoriteListRequest;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListResponse;
import org.greedy.ddarahang.api.service.FavoriteListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private final FavoriteListService favoriteListService;

    @PostMapping("/list")
    public ResponseEntity<FavoriteListResponse> createFavoriteList(@RequestBody CreateFavoriteListRequest request) {
        return ResponseEntity.ok(favoriteListService.createFavoriteListResponse(request.listName()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FavoriteListResponse>> getFavoriteLists() {
        return ResponseEntity.ok(favoriteListService.getFavoriteLists());
    }
}
