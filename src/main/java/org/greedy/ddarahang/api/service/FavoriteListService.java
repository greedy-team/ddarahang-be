package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.*;
import org.greedy.ddarahang.common.exception.NotFoundFavoriteListException;
import org.greedy.ddarahang.db.favoriteList.FavoriteList;
import org.greedy.ddarahang.db.favoriteList.FavoritePlace;
import org.greedy.ddarahang.db.favoriteList.FavoritePlaceRepository;
import org.greedy.ddarahang.db.favoriteList.FavoriteListRepository;
import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.place.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteListService {

    private final FavoriteListRepository favoriteListRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final PlaceRepository placeRepository;

    public List<FavoriteListNonLoginResponse> createFavoriteListNonLogin(CreateFavoriteListNonLoginRequest request) {
        List<Long> placeIds = request.placeIds();

        Map<Long, Long> orderMap = new HashMap<>();
        for (int i = 0; i < placeIds.size(); i++) {
            orderMap.put(placeIds.get(i), (long) i + 1);
        }

        List<Place> places = placeRepository.findAllById(placeIds);

        return places.stream()
                .sorted(Comparator.comparingLong(p -> orderMap.getOrDefault(p.getId(), Long.MAX_VALUE)))
                .map(place -> new FavoriteListNonLoginResponse(
                        orderMap.get(place.getId()),
                        place.getName(),
                        place.getAddress(),
                        place.getLatitude(),
                        place.getLongitude()
                ))
                .toList();
    }

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

        return FavoriteListResponse.from(savedFavoriteList);
    }

    @Transactional(readOnly = true)
    public List<FavoriteListResponse> getFavoriteLists() {
        return favoriteListRepository.findAll().stream()
                .map(FavoriteListResponse::from)
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
