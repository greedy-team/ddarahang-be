package org.greedy.ddarahang.api.service;

import org.greedy.ddarahang.api.dto.favoriteDTO.CreateFavoriteListNonLoginRequest;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListNonLoginResponse;
import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.place.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteListServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private FavoriteListService favoriteListService;

    @Test
    void 비로그인_찜목록_생성_서비스_코드_테스트() {

        // given
        List<Long> requestIds = List.of(3L, 1L, 2L);
        CreateFavoriteListNonLoginRequest request = new CreateFavoriteListNonLoginRequest(requestIds);

        Place p1 = Place.builder()
                .id(1L).name("식당A").address("주소A").latitude(11.1).longitude(111.1).build();
        Place p2 = Place.builder()
                .id(2L).name("식당B").address("주소B").latitude(22.2).longitude(222.2).build();
        Place p3 = Place.builder()
                .id(3L).name("식당C").address("주소C").latitude(33.3).longitude(333.3).build();

        when(placeRepository.findAllById(requestIds))
                .thenReturn(List.of(p2, p3, p1)); // JPA 의 리스트 순서를 보장하지 않는 특징을 구현

        // when
        List<FavoriteListNonLoginResponse> result =
                favoriteListService.createFavoriteListNonLogin(request);

        // then
        assertEquals(3, result.size()); // 반환하는 찜 목록 내 장소의 개수 검증

        assertEquals(1L, result.get(0).orderInPlace()); // p3, 첫번째 장소의 목록 내 순서(orderInPlace) 검증
        assertEquals("식당C", result.get(0).placeName()); // 찜 장소의 장소명 검증

        assertEquals(11.1, result.get(1).latitude()); // p1, 찜 장소의 latitude 검증

        assertEquals(222.2, result.get(2).longitude()); // p2, 찜 장소의 longitude 검증

        verify(placeRepository).findAllById(requestIds); // findAllById 가 한 번 호출되었는지 검증
    }
}
