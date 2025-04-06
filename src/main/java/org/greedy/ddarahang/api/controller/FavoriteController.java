package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.CreateFavoriteListRequest;
import org.greedy.ddarahang.api.dto.favoriteDTO.CreateFavoriteListResponse;
import org.greedy.ddarahang.api.service.FavoriteListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private final FavoriteListService favoriteListService;

    @PostMapping("/list")
    public ResponseEntity<CreateFavoriteListResponse> createFavoriteList(@RequestBody CreateFavoriteListRequest request) {
        return ResponseEntity.ok(favoriteListService.createFavoriteListResponse(request.listName()));
    }
}
