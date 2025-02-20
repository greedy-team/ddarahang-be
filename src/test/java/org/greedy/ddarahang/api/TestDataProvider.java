package org.greedy.ddarahang.api;

import org.greedy.ddarahang.api.dto.TravelCourseResponse;

import java.time.LocalDate;
import java.util.List;

public class TestDataProvider {
    public static List<TravelCourseResponse> getMockTravelCouresResponses(){
        return List.of(
                new TravelCourseResponse(
                        1L, "calm down man", "침착맨 삼국지 완전판",
                        "https://www.youtube.com/watch?v=hnanNlDbsE4",
                        22613460L, LocalDate.of(2020, 10, 4)
                ),
                new TravelCourseResponse(
                        2L, "calm down man", "유사과학 월드컵 (이과답답주의)",
                        "https://www.youtube.com/watch?v=Z7_WWJEj-j8",
                        21266202L, LocalDate.of(2021, 4, 12)
                ),
                new TravelCourseResponse(
                        3L, "calm down man", "음모론 & 미스터리 월드컵",
                        "https://www.youtube.com/watch?v=qRUxCTEtKMI",
                        7240503L, LocalDate.of(2021, 7, 13)
                )
        );
    }
}
