package org.greedy.ddarahang.api.controller;

import io.restassured.RestAssured;
import org.greedy.ddarahang.common.AllFixture;
import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.region.RegionRepository;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetail;
import org.greedy.ddarahang.db.video.Video;
import org.greedy.ddarahang.db.video.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
    private RegionRepository regionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country country;
    private Region region;
    private Video video;
    private TravelCourse travelCourse;
    private TravelCourseDetail travelCourseDetail;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        travelCourseRepository.deleteAll();
        videoRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();

        prepareTestData();
    }

    private void prepareTestData() {
        country = countryRepository.save(AllFixture.getMockCountry());
        region = regionRepository.save(AllFixture.getMockRegion(country));
        video = videoRepository.save(AllFixture.getMockVideo(LocalDate.now()));
        travelCourse = travelCourseRepository.save(AllFixture.getMockTravelCourse(video, country, region));
    }

    /**
     * 🔹 GET: /travelcourses
     */
    @Nested
    class 여행_목록_조회_API {

        @Test
        void 여행_목록_조회_성공하면_200_응답을_보낸다() {
            RestAssured.given()
                    .param("countryName", country.getName())
                    .param("regionName", region.getName())
                    .when()
                    .get("/api/v1/travelcourses")
                    .then()
                    .statusCode(200);
        }

        @Test
        void 여행_목록_조회_지역명이_없어도_200_응답을_보낸다() {
            RestAssured.given()
                    .param("countryName", country.getName())
                    .param("regionName", "")
                    .when()
                    .get("/api/v1/travelcourses")
                    .then()
                    .statusCode(200);
        }
    }


    /**
     * 🔹 GET: /travelcourses/{id}
     */
    @Nested
    class 여행_상세_조회_API {

        @Test
        void 여행_상세_조회_성공하면_200_응답을_보낸다() {
            RestAssured.given()
                    .pathParam("id", travelCourse.getId())
                    .when()
                    .get("/api/v1/travelcourses/{id}")
                    .then()
                    .statusCode(200);
        }
    }


    /**
     * 🔹 GET: /travelcourses/uploaddate
     */
    @Nested
    class 여행_목록_조회_업로드_날짜_내림차순_API {

        @Test
        void 여행_목록_조회_업로드_날짜_내림차순_성공하면_200_응답을_보낸다() {
            RestAssured.given()
                    .param("countryName", country.getName())
                    .param("regionName", region.getName())
                    .when()
                    .get("/api/v1/travelcourses/uploaddate")
                    .then()
                    .statusCode(200);
        }

        @Test
        void 여행_목록_조회_업로드_날짜_내림차순_지역명이_없어도_200_응답을_보낸다() {
            RestAssured.given()
                    .param("countryName", country.getName())
                    .param("regionName", "")
                    .when()
                    .get("/api/v1/travelcourses/uploaddate")
                    .then()
                    .statusCode(200);
        }
    }


    /**
     * 🔹 GET: /travelcourses/viewcount
     */
    @Nested
    class 여행_목록_조회_조회수_내림차순_API {

        @Test
        void 여행_목록_조회_조회수_내림차순_성공하면_200_응답을_보낸다() {
            RestAssured.given()
                    .param("countryName", country.getName())
                    .param("regionName", region.getName())
                    .when()
                    .get("/api/v1/travelcourses/viewcount")
                    .then()
                    .statusCode(200);
        }

        @Test
        void 여행_목록_조회_조회수_내림차순_지역명이_없어도_200_응답을_보낸다() {
            RestAssured.given()
                    .param("countryName", country.getName())
                    .param("regionName", "")
                    .when()
                    .get("/api/v1/travelcourses/viewcount")
                    .then()
                    .statusCode(200);
        }
    }
}
