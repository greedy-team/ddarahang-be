package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListResponse;
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
    public FavoriteListResponse createFavoriteListResponse(String listName) {
        FavoriteList favoriteList = FavoriteList.builder()
                .listName(listName)
                .build();
        FavoriteList saved = favoriteListRepository.save(favoriteList);
        return FavoriteListResponse.from(saved);
    }

    public List<FavoriteListResponse> getFavoriteLists() {
        return favoriteListRepository.findAll()
                .stream().map(FavoriteListResponse::from)
                .toList();
    }
}
