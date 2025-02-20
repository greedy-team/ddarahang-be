package org.greedy.ddarahang.api.service;

import org.greedy.ddarahang.api.TestDataProvider;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private List<TravelCourseResponse> mockResponses;

    @BeforeEach
    void setUp(){
        Mockito.reset(travelCourseRepository);
        mockResponses = TestDataProvider.getMockTravelCouresResponses();
    }

    @Test
    void regionName이_없는_경우() {
        //Given
        String countryName = "대한민국";
        String regionName = "";
        TravelCourse travelCourse = new TravelCourse(

        );
        when(travelCourseRepository.findByCountryName("대한민국")).thenReturn(List.of(travelCourse));

        //When
        List<TravelCourseResponse> result = travelCourseService.getTravelCourses(countryName, regionName);

        //Then
        verify(travelCourseRepository, times(1)).findByCountryName("대한민국");
        verify(travelCourseRepository, never()).findByRegionName(anyString());

        assertEquals(1, result.size());
    }

//    @Test
//    void regionName이_있는_경우() {
//        //Given
//        when(travelCourseRepository.findByRegionName("서울")).thenReturn(List.of());
//
//        //When
//        List<TravelCourseResponse> result = travelCourseService.getTravelCourses(request);
//
//        //Then
//        verify(travelCourseRepository, never()).findByCountryName(anyString());
//        verify(travelCourseRepository, times(1)).findByRegionName("서울");
//
//        assertEquals(1, result.size());
//    }
}