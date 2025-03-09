package org.greedy.ddarahang.api.service;

import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.fixture.VideoFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class TravelCourseSortingServiceTest extends TravelCourseServiceTest {

    @Test
    void 여행코스_조회수순_정렬_테스트(){
        //given
        videoRepository.save(VideoFixture.getMockVideo_1(LocalDate.now()));
        videoRepository.save(VideoFixture.getMockVideo_2(LocalDate.now().minusDays(1)));

        //when
        List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses("viewCount", country.getName(), region.getName());

        //then
        List<Long> viewCounts = responses.stream()
                .map(TravelCourseListResponse::viewCount)
                .collect(Collectors.toList());
        assertThat(viewCounts).isSortedAccordingTo((v1, v2) -> Long.compare(v2, v1));
    }

    @Test
    void 여행코스_날짜순_정렬_테스트(){
        //given
        videoRepository.save(VideoFixture.getMockVideo_1(LocalDate.now()));
        videoRepository.save(VideoFixture.getMockVideo_1(LocalDate.now()));

        //when
        List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses("uploadDate", country.getName(), region.getName());

        //then
        List<LocalDate> uploadDates = responses.stream()
                .map(TravelCourseListResponse::uploadDate)
                .collect(Collectors.toList());
        assertThat(uploadDates).isSortedAccordingTo((d1, d2) -> d2.compareTo(d1));
    }
}
