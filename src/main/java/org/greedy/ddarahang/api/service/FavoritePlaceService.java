package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.AddFavoritePlaceRequest;
import org.greedy.ddarahang.api.dto.favoriteDTO.DeleteFavoritePlaceResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoritePlaceResponse;
import org.greedy.ddarahang.common.exception.DuplicateFavoritePlaceException;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoriteListRepository favoriteListRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteListPlaceRepository favoriteListPlaceRepository;

    public FavoritePlaceResponse addFavoritePlace(AddFavoritePlaceRequest request) {

        FavoriteList favoriteList = favoriteListRepository.findById(request.favoriteListId())
                .orElseThrow(() -> new NotFoundFavoriteListException("해당 Id를 갖는 찜 목록을 찾을 수 없습니다."));

        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new NotFoundPlaceException("해당 Id를 갖는 장소를 찾을 수 없습니다."));

        boolean isAlreadyAdded = favoriteListPlaceRepository.existsByFavoriteListIdAndPlaceId(
                request.favoriteListId(), request.placeId());

        if (isAlreadyAdded) {
            throw new DuplicateFavoritePlaceException("해당 장소는 이미 찜 목록에 추가되어 있습니다.");
        }

        int orderInList = favoriteListPlaceRepository.countByFavoriteListId(request.favoriteListId()) + 1;

        FavoriteListPlace favoriteListPlace = FavoriteListPlace.builder()
                .favoriteList(favoriteList)
                .place(place)
                .orderInList(orderInList)
                .build();

        favoriteListPlaceRepository.save(favoriteListPlace);

        return FavoritePlaceResponse.from(place, orderInList);
    }

    public DeleteFavoritePlaceResponse deleteFavoritePlace(Long favoriteListId, Long placeId) {

        FavoriteListPlace favoriteListPlace = favoriteListPlaceRepository
                .findByFavoriteListIdAndPlaceId(favoriteListId, placeId)
                .orElseThrow(() -> new NotFoundFavoritePlaceException("해당 찜 장소를 찾을 수 없습니다."));

        String listName = favoriteListPlace.getFavoriteList().getListName();
        String placeName = favoriteListPlace.getPlace().getName();

        favoriteListPlaceRepository.delete(favoriteListPlace);

        String message = listName + " 의 " + placeName + " 이(가) 삭제되었습니다.";
        return new DeleteFavoritePlaceResponse(message);
    }
}
