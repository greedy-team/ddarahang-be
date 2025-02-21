package org.greedy.ddarahang.api.controller;

import io.restassured.RestAssured;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.greedy.ddarahang.common.AllFixture;
import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.region.RegionRepository;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
import org.greedy.ddarahang.db.video.Video;
import org.greedy.ddarahang.db.video.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TravelCourseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TravelCourseRepository travelCourseRepository;

    @Autowired
    private TravelCourseDetailRepository travelCourseDetailRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TravelCourseService travelCourseService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        travelCourseRepository.deleteAll();
        videoRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void 여행_목록_조회_성공하면_200_응답을_보낸다() {
        //Given
        String countryName = "대한민국";
        String regionName = "서울";

        Country mockCountry = AllFixture.getMockCountry();
        Country country = countryRepository.save(mockCountry);
        Region mockRegion = AllFixture.getMockRegion(country);
        Region region = regionRepository.save(mockRegion);
        Video mockVideo = AllFixture.getMockVideo(LocalDate.now());
        Video video = videoRepository.save(mockVideo);
        TravelCourse mockTravelCourse = AllFixture.getMockTravelCourse(video, country, region);
        travelCourseRepository.save(mockTravelCourse);

        // When
        RestAssured.given()
                .param("countryName", countryName)
                .param("regionName", regionName)
                .when()
                .get("/api/v1/travelcourses")
                .then()
                .statusCode(200); // Then
    }

    @Test
    void 여행_상세_조회_성공하면_200_응답을_보낸다() {
        //Given

        Country mockCountry = AllFixture.getMockCountry();
        Country country = countryRepository.save(mockCountry);
        Region mockRegion = AllFixture.getMockRegion(country);
        Region region = regionRepository.save(mockRegion);
        Video mockVideo = AllFixture.getMockVideo(LocalDate.now());
        Video video = videoRepository.save(mockVideo);
        TravelCourse mockTravelCourse = AllFixture.getMockTravelCourse(video, country, region);
        TravelCourse saveTravelCourse = travelCourseRepository.save(mockTravelCourse);
        Long id = saveTravelCourse.getId();

        // When
        RestAssured.given()
                .pathParam("id", id)
                .when()
                .get("/api/v1/travelcourses/{id}")
                .then()
                .statusCode(200); // Then
    }
}
