package org.greedy.ddarahang.api.service;

import org.greedy.ddarahang.api.dto.TravelCourseRequest;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelCourseServiceTest {

    @Mock
    private TravelCourseRepository travelCourseRepository;

    @InjectMocks
    private TravelCourseService travelCourseService;

    private TravelCourse mockTravelCourse;

    @BeforeEach
    void setUp(){
        Mockito.reset(travelCourseRepository);

        Video mockVideo = mock(Video.class);
        when(mockVideo.getCreator()).thenReturn("Test Creator");
        when(mockVideo.getTitle()).thenReturn("Test Title");
        when(mockVideo.getThumbnailUrl()).thenReturn("http://test.com/thumbnail.jpg");
        when(mockVideo.getViewCount()).thenReturn(100L);
        when(mockVideo.getUploadDate()).thenReturn(LocalDate.of(2024, 2, 20));

        mockTravelCourse = mock(TravelCourse.class);
        when(mockTravelCourse.getId()).thenReturn(1L);
        when(mockTravelCourse.getVideo()).thenReturn(mockVideo);
    }

    @Test
    void regionName이_없는_경우() {
        //Given
        TravelCourseRequest request = new TravelCourseRequest("대한민국", null);
        when(travelCourseRepository.findByCountryName("대한민국")).thenReturn(List.of(mockTravelCourse));

        //When
        List<TravelCourseResponse> result = travelCourseService.getTravelCourses(request);

        //Then
        verify(travelCourseRepository, times(1)).findByCountryName("대한민국");
        verify(travelCourseRepository, never()).findByRegionName(anyString());

        assertEquals(1, result.size());
    }

    @Test
    void regionName이_있는_경우() {
        //Given
        TravelCourseRequest request = new TravelCourseRequest("대한민국", "서울");
        when(travelCourseRepository.findByRegionName("서울")).thenReturn(List.of(mockTravelCourse));

        //When
        List<TravelCourseResponse> result = travelCourseService.getTravelCourses(request);

        //Then
        verify(travelCourseRepository, never()).findByCountryName(anyString());
        verify(travelCourseRepository, times(1)).findByRegionName("서울");

        assertEquals(1, result.size());
    }
}