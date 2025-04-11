package org.greedy.ddarahang.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.*;
import org.greedy.ddarahang.api.service.FavoriteListService;
import org.greedy.ddarahang.api.service.FavoritePlaceService;
import org.greedy.ddarahang.db.favoriteList.FavoriteListRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteListService favoriteListService;
    private final FavoritePlaceService favoritePlaceService;
    private final FavoriteListRepository favoriteListRepository;

    @PostMapping("/list")
    public ResponseEntity<FavoriteListResponse> createFavoriteList(@RequestBody CreateFavoriteListRequest request) {
        return ResponseEntity.ok(favoriteListService.createFavoriteList(request.listName(), request.description()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FavoriteListResponse>> getFavoriteLists() {
        return ResponseEntity.ok(favoriteListService.getFavoriteLists());
    }

    @DeleteMapping("/list/{favoriteListId}")
    public ResponseEntity<DeleteFavoriteListResponse> deleteFavoriteList(@PathVariable Long favoriteListId) {
        DeleteFavoriteListResponse response = favoriteListService.deleteFavoriteList(favoriteListId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/places")
    public ResponseEntity<FavoritePlaceResponse> placeFavoriteList(@RequestBody AddFavoritePlaceRequest request) {
        return ResponseEntity.ok(favoritePlaceService.addFavoritePlace(request));
    }

    @DeleteMapping("/list/{favoriteListId}/places/{placeId}")
    public ResponseEntity<DeleteFavoritePlaceResponse> deleteFavoritePlace(@PathVariable Long favoriteListId,
                                                                           @PathVariable Long placeId) {
        DeleteFavoritePlaceResponse response = favoritePlaceService.deleteFavoritePlace(favoriteListId, placeId);
        return ResponseEntity.ok(response);
    }

}
