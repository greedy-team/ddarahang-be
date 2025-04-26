package org.greedy.ddarahang.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.*;
import org.greedy.ddarahang.api.service.FavoriteListService;
import org.greedy.ddarahang.api.service.FavoritePlaceService;
import org.greedy.ddarahang.api.spec.FavoriteSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteController implements FavoriteSpecification {

    private final FavoriteListService favoriteListService;
    private final FavoritePlaceService favoritePlaceService;

    @Override
    @PostMapping("/list")
    public ResponseEntity<FavoriteListResponse> createFavoriteList(@Valid @RequestBody CreateFavoriteListRequest request) {
        FavoriteListResponse response = favoriteListService.createFavoriteList(request.listName(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<List<FavoriteListResponse>> getFavoriteLists() {
        return ResponseEntity.ok(favoriteListService.getFavoriteLists());
    }

    @Override
    @DeleteMapping("/list/{favoriteListId}")
    public ResponseEntity<DeleteFavoriteListResponse> deleteFavoriteList(@PathVariable Long favoriteListId) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/places")
    public ResponseEntity<FavoritePlaceResponse> addFavoritePlace(@Valid @RequestBody AddFavoritePlaceRequest request) {
        FavoritePlaceResponse response = favoritePlaceService.addFavoritePlace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @DeleteMapping("/list/{favoriteListId}/places/{placeId}")
    public ResponseEntity<DeleteFavoritePlaceResponse> deleteFavoritePlace(@PathVariable Long favoriteListId,
                                                                           @PathVariable Long placeId) {
        return ResponseEntity.noContent().build();
    }
}
