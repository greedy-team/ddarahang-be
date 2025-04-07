package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.AddFavoritePlace;
import org.greedy.ddarahang.api.dto.favoriteDTO.DeleteFavoritePlaceResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoritePlaceResponse;
import org.greedy.ddarahang.common.exception.NotFoundFavoriteListException;
import org.greedy.ddarahang.common.exception.NotFoundFavoritePlaceException;
import org.greedy.ddarahang.common.exception.NotFoundPlaceException;
import org.greedy.ddarahang.db.favoriteList.FavoriteList;
import org.greedy.ddarahang.db.favoriteList.FavoriteListPlace;
import org.greedy.ddarahang.db.favoriteList.FavoriteListPlaceRepository;
import org.greedy.ddarahang.db.favoriteList.FavoriteListRepository;
import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.place.PlaceRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoriteListRepository favoriteListRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteListPlaceRepository favoriteListPlaceRepository;

    @Transactional
    public FavoritePlaceResponse addFavoritePlace(AddFavoritePlace request) {

        FavoriteList favoriteList = favoriteListRepository.findById(request.favoriteListId())
                .orElseThrow(() -> new NotFoundFavoriteListException("찜 목록을 찾을 수 없습니다."));

        Place place = placeRepository.findByLatitudeAndLongitude(request.latitude(), request.longitude())
                .orElseThrow(() -> new NotFoundPlaceException("해당 위경도의 장소가 존재하지 않습니다."));

        int orderInList = favoriteList.getFavoriteListPlaces().size() + 1;

        FavoriteListPlace favoriteListPlace = FavoriteListPlace.builder()
                .favoriteList(favoriteList)
                .place(place)
                .orderInList(orderInList)
                .build();
        favoriteListPlaceRepository.save(favoriteListPlace);

        return FavoritePlaceResponse.from(place, orderInList);
    }

    @Transactional
    public DeleteFavoritePlaceResponse deleteFavoritePlace(Long favoriteListId, Long placeId) {

        FavoriteList favoriteList = favoriteListRepository.findById(favoriteListId)
                .orElseThrow(() -> new NotFoundFavoriteListException("해당 찜 목록을 찾을 수 없습니다."));

        FavoriteListPlace favoriteListPlace = favoriteList.getFavoriteListPlaces().stream()
                .filter(flp -> flp.getPlace().getId().equals(placeId))
                .findFirst()
                .orElseThrow(() -> new NotFoundFavoritePlaceException("해당 찜 장소를 찾을 수 없습니다."));

        String listName = favoriteList.getListName();
        String placeName = favoriteListPlace.getPlace().getName();

        favoriteList.getFavoriteListPlaces().remove(favoriteListPlace);
        favoriteListPlaceRepository.delete(favoriteListPlace);

        String message = listName + " 의 " + placeName + " 이(가) 삭제되었습니다.";
        return new DeleteFavoritePlaceResponse(message);
    }

}
