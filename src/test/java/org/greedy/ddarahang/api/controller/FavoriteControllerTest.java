package org.greedy.ddarahang.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.greedy.ddarahang.api.dto.favoriteDTO.CreateFavoriteListNonLoginRequest;
import org.greedy.ddarahang.api.dto.favoriteDTO.FavoriteListNonLoginResponse;
import org.greedy.ddarahang.api.service.FavoriteListService;
import org.greedy.ddarahang.api.service.FavoritePlaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FavoriteController.class)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteListService favoriteListService;

    @MockitoBean
    private FavoritePlaceService favoritePlaceService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void POST_favorite_비로그인_찜목록_생성() throws Exception {

        // given
        List<FavoriteListNonLoginResponse> stubResponse = List.of(
                new FavoriteListNonLoginResponse(1L, "식당C", "주소C", 33.3, 333.3, "음식"),
                new FavoriteListNonLoginResponse(2L, "식당A", "주소A", 11.1, 111.1, "음식"),
                new FavoriteListNonLoginResponse(3L, "식당B", "주소B", 22.2, 222.2, "음식")
        );
        when(favoriteListService.createFavoriteListNonLogin(any(CreateFavoriteListNonLoginRequest.class)))
                .thenReturn(stubResponse);

        CreateFavoriteListNonLoginRequest reqDto =
                new CreateFavoriteListNonLoginRequest(List.of(3L, 1L, 2L));
        String json = mapper.writeValueAsString(reqDto);

        // when & then
        mockMvc.perform(post("/api/v1/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // 배열 길이
                .andExpect(jsonPath("$.length()", is(3)))
                // 첫 번째 원소 검증
                .andExpect(jsonPath("$[0].orderInPlace", is(1)))
                .andExpect(jsonPath("$[0].placeName", is("식당C")))
                // 두 번째 원소 일부 필드
                .andExpect(jsonPath("$[1].latitude", is(11.1)))
                // 세 번째 원소 일부 필드
                .andExpect(jsonPath("$[2].longitude", is(222.2)));

        ArgumentCaptor<CreateFavoriteListNonLoginRequest> captor =
                ArgumentCaptor.forClass(CreateFavoriteListNonLoginRequest.class);
        verify(favoriteListService, times(1))
                .createFavoriteListNonLogin(captor.capture());
        List<Long> capturedIds = captor.getValue().placeIds();
        assertThat(capturedIds, contains(3L, 1L, 2L));
    }
}
