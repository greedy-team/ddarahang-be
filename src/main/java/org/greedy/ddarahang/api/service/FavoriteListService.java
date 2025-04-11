package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.DeleteFavoriteListResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoritePlaceResponse;
import org.greedy.ddarahang.common.exception.NotFoundFavoriteListException;
import org.greedy.ddarahang.db.favoriteList.FavoriteList;
import org.greedy.ddarahang.db.favoriteList.FavoriteListPlace;
import org.greedy.ddarahang.db.favoriteList.FavoriteListPlaceRepository;
import org.greedy.ddarahang.db.favoriteList.FavoriteListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteListService {

    private final FavoriteListRepository favoriteListRepository;
    private final FavoriteListPlaceRepository favoriteListPlaceRepository;

    @Transactional
    public FavoriteListResponse createFavoriteList(String listName, String description) {
        FavoriteList favoriteList = FavoriteList.builder()
                .listName(listName)
                .description(description)
                .build();

        FavoriteList savedFavoriteList = favoriteListRepository.save(favoriteList);

        List<FavoritePlaceResponse> favoritePlaces = favoriteListPlaceRepository
                .findAllByFavoriteListId(savedFavoriteList.getId())
                .stream()
                .map(FavoritePlaceResponse::from)
                .toList();

        return FavoriteListResponse.from(savedFavoriteList, favoritePlaces);
    }

    public List<FavoriteListResponse> getFavoriteLists() {
        return favoriteListRepository.findAll()
                .stream()
                .map(favoriteList -> {
                    List<FavoritePlaceResponse> places = favoriteListPlaceRepository
                            .findAllByFavoriteListId(favoriteList.getId())
                            .stream()
                            .map(FavoritePlaceResponse::from)
                            .toList();
                    return FavoriteListResponse.from(favoriteList, places);
                })
                .toList();
    }

    @Transactional
    public DeleteFavoriteListResponse deleteFavoriteList(Long favoriteListId) {
        FavoriteList favoriteList = favoriteListRepository.findById(favoriteListId)
                .orElseThrow(() -> new NotFoundFavoriteListException("해당 찜 목록을 찾을 수 없습니다."));

        List<FavoriteListPlace> places = favoriteListPlaceRepository.findAllByFavoriteListId(favoriteListId);

        favoriteListPlaceRepository.deleteAll(places);

        favoriteListPlaceRepository.flush();

        favoriteListRepository.delete(favoriteList);

        return new DeleteFavoriteListResponse(favoriteList.getListName() + " 이(가) 삭제되었습니다.");
    }
}
