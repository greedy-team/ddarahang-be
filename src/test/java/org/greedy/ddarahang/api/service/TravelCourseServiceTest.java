package org.greedy.ddarahang.api.service;

import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.AllFixture;
import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.region.RegionRepository;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.video.Video;
import org.greedy.ddarahang.db.video.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class TravelCourseServiceTest {

    @Autowired
    private TravelCourseRepository travelCourseRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TravelCourseService travelCourseService;

    @Test
    void regionName이_없는_경우() {
        //Given
        String countryName = "대한민국";
        String regionName = "";

        System.out.println(travelCourseRepository);

        Country mockCountry = AllFixture.getMockCountry();
        Country country = countryRepository.save(mockCountry);
        Region mockRegion = AllFixture.getMockRegion(country);
        Region region = regionRepository.save(mockRegion);
        Video mockVideo = AllFixture.getMockVideo(LocalDate.now());
        Video video = videoRepository.save(mockVideo);
        TravelCourse mockTravelCourse = AllFixture.getMockTravelCourse(video, country, region);
        travelCourseRepository.save(mockTravelCourse);

        //When
        List<TravelCourseResponse> responses = travelCourseService.getTravelCourses(countryName, regionName);

        //Then
        assertEquals(1, responses.size());
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