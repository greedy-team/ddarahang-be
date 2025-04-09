package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.DeleteFavoriteListResponse;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListResponse;
import org.greedy.ddarahang.common.exception.NotFoundFavoriteListException;
import org.greedy.ddarahang.db.favoriteList.FavoriteList;
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

    @Transactional
    public FavoriteListResponse createFavoriteList(String listName, String description) {
        FavoriteList favoriteList = FavoriteList.builder()
                .listName(listName)
                .description(description)
                .build();
        FavoriteList saved = favoriteListRepository.save(favoriteList);
        return FavoriteListResponse.from(saved);
    }

    public List<FavoriteListResponse> getFavoriteLists() {
        return favoriteListRepository.findAll()
                .stream().map(FavoriteListResponse::from)
                .toList();
    }

    @Transactional
    public DeleteFavoriteListResponse deleteFavoriteList(Long favoriteListId) {
        FavoriteList favoriteList = favoriteListRepository.findById(favoriteListId)
                .orElseThrow(() -> new NotFoundFavoriteListException("해당 찜 목록을 찾을 수 없습니다."));
        String listName = favoriteList.getListName();
        favoriteListRepository.delete(favoriteList);
        return new DeleteFavoriteListResponse(listName + " 이(가) 삭제되었습니다.");
    }

}
