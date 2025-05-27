package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.AddFavoritePlaceRequest;
import org.greedy.ddarahang.api.dto.favoriteDTO.DeleteFavoritePlaceResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoritePlaceResponse;
import org.greedy.ddarahang.common.exception.ConflictDataException;
import org.greedy.ddarahang.common.exception.ErrorMessage;
import org.greedy.ddarahang.common.exception.NotFoundDataException;
import org.greedy.ddarahang.db.favoriteList.FavoriteList;
import org.greedy.ddarahang.db.favoriteList.FavoritePlace;
import org.greedy.ddarahang.db.favoriteList.FavoritePlaceRepository;
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

    private final PlaceRepository placeRepository;
    private final FavoriteListRepository favoriteListRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;

    public FavoritePlaceResponse addFavoritePlace(AddFavoritePlaceRequest request) {

        FavoriteList favoriteList = favoriteListRepository.findById(request.favoriteListId())
                .orElseThrow(() -> new NotFoundDataException(ErrorMessage.NOT_FOUND_FAVORITE_LIST));

        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new NotFoundDataException(ErrorMessage.NOT_FOUND_PLACE));

        boolean isAlreadyAdded = favoritePlaceRepository.existsByFavoriteListIdAndPlaceId(
                request.favoriteListId(), request.placeId());

        if (isAlreadyAdded) {
            throw new ConflictDataException(ErrorMessage.DUPLICATE_FAVORITE_PLACE);
        }

        int orderInList = favoritePlaceRepository.countByFavoriteListId(request.favoriteListId()) + 1;

        FavoritePlace favoritePlace = FavoritePlace.builder()
                .favoriteList(favoriteList)
                .place(place)
                .orderInList(orderInList)
                .build();

        favoritePlaceRepository.save(favoritePlace);

        return FavoritePlaceResponse.from(place, orderInList);
    }

    public DeleteFavoritePlaceResponse deleteFavoritePlace(Long favoriteListId, Long placeId) {

        FavoritePlace favoritePlace = favoritePlaceRepository
                .findByFavoriteListIdAndPlaceId(favoriteListId, placeId)
                .orElseThrow(() -> new NotFoundDataException(ErrorMessage.NOT_FOUND_FAVORITE_PLACE));

        String listName = favoritePlace.getFavoriteList().getListName();
        String placeName = favoritePlace.getPlace().getName();

        favoritePlaceRepository.delete(favoritePlace);

        String message = listName + " 의 " + placeName + " 이(가) 삭제되었습니다.";
        return new DeleteFavoritePlaceResponse(message);
    }
}
