package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.DeleteFavoriteListResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoritePlaceResponse;
import org.greedy.ddarahang.common.exception.NotFoundFavoriteListException;
import org.greedy.ddarahang.db.favoriteList.FavoriteList;
import org.greedy.ddarahang.db.favoriteList.FavoritePlace;
import org.greedy.ddarahang.db.favoriteList.FavoritePlaceRepository;
import org.greedy.ddarahang.db.favoriteList.FavoriteListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteListService {

    private final FavoriteListRepository favoriteListRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;

    public FavoriteListResponse createFavoriteList(String listName, String description) {
        FavoriteList favoriteList = FavoriteList.builder()
                .listName(listName)
                .description(description)
                .build();

        FavoriteList savedFavoriteList = favoriteListRepository.save(favoriteList);

        List<FavoritePlaceResponse> favoritePlaces = favoritePlaceRepository
                .findAllByFavoriteListId(savedFavoriteList.getId())
                .stream()
                .map(FavoritePlaceResponse::from)
                .toList();

        return FavoriteListResponse.from(savedFavoriteList, favoritePlaces);
    }

    @Transactional(readOnly = true)
    public List<FavoriteListResponse> getFavoriteLists() {

        List<FavoriteList> favoriteLists = favoriteListRepository.findAll();

        List<Long> favoriteListIds = favoriteLists.stream()
                .map(FavoriteList::getId)
                .toList();

        List<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllByFavoriteListIdIn(favoriteListIds);

        Map<Long, List<FavoritePlace>> groupedPlaces = favoritePlaces.stream()
                .collect(Collectors.groupingBy(flp -> flp.getFavoriteList().getId()));

        return favoriteLists.stream()
                .map(favoriteList -> {
                    List<FavoritePlaceResponse> places = groupedPlaces
                            .getOrDefault(favoriteList.getId(), List.of())
                            .stream()
                            .map(FavoritePlaceResponse::from)
                            .toList();
                    return FavoriteListResponse.from(favoriteList, places);
                })
                .toList();
    }

    public DeleteFavoriteListResponse deleteFavoriteList(Long favoriteListId) {
        FavoriteList favoriteList = favoriteListRepository.findById(favoriteListId)
                .orElseThrow(() -> new NotFoundFavoriteListException("해당 찜 목록을 찾을 수 없습니다."));

        List<FavoritePlace> places = favoritePlaceRepository.findAllByFavoriteListId(favoriteListId);

        favoritePlaceRepository.deleteAll(places);

        favoriteListRepository.delete(favoriteList);

        return new DeleteFavoriteListResponse(favoriteList.getListName() + " 이(가) 삭제되었습니다.");
    }
}
