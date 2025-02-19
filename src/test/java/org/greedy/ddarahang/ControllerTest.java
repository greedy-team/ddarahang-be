package org.greedy.ddarahang;

import org.greedy.ddarahang.api.controller.TravelCourseController;
import org.greedy.ddarahang.api.dto.TravelCourseRequest;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock
    private TravelCourseService travelCourseService;

    @InjectMocks
    private TravelCourseController travelCourseController;

    @BeforeEach
    void setUp() {
        Mockito.reset(travelCourseService);
    }

    @Test
    void 여행_목록_조회() {
        // Given
        TravelCourseRequest request = new TravelCourseRequest("대한민국", null);

        // 여러 개의 mock 데이터 준비
        TravelCourseResponse response1 = new TravelCourseResponse(
                1L, "calm down man", "침착맨 삼국지 완전판",
                "https://www.youtube.com/watch?v=hnanNlDbsE4",
                22613460L, LocalDate.of(2020, 10, 4)
        );

        TravelCourseResponse response2 = new TravelCourseResponse(
                2L, "calm down man", "유사과학 월드컵 (이과답답주의)",
                "https://www.youtube.com/watch?v=Z7_WWJEj-j8",
                21266202L, LocalDate.of(2021, 4, 12)
        );

        TravelCourseResponse response3 = new TravelCourseResponse(
                3L, "calm down man", "음모론 & 미스터리 월드컵",
                "https://www.youtube.com/watch?v=qRUxCTEtKMI",
                7240503L, LocalDate.of(2021, 7, 13)
        );

        when(travelCourseService.getTravelCourses(request)).thenReturn(List.of(response1, response2, response3));

        // When
        List<TravelCourseResponse> result = travelCourseController.getTravelCourses(request);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).title()).isEqualTo("침착맨 삼국지 완전판");
        assertThat(result.get(0).thumbnailUrl()).isEqualTo("https://www.youtube.com/watch?v=hnanNlDbsE4");
        assertThat(result.get(1).title()).isEqualTo("유사과학 월드컵 (이과답답주의)");
        assertThat(result.get(1).viewCount()).isEqualTo(21266202L);
        assertThat(result.get(2).title()).isEqualTo("음모론 & 미스터리 월드컵");
        assertThat(result.get(2).uploadDate()).isEqualTo(LocalDate.of(2021, 7, 13));

        verify(travelCourseService, times(1)).getTravelCourses(request);
    }

}
