package org.greedy.ddarahang.api.controller;

import io.restassured.RestAssured;
import org.greedy.ddarahang.common.BaseTest;
import org.greedy.ddarahang.common.fixture.CountryFixture;
import org.greedy.ddarahang.common.fixture.RegionFixture;
import org.greedy.ddarahang.common.fixture.TravelCourseFixture;
import org.greedy.ddarahang.common.fixture.VideoFixture;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class TravelCourseControllerTest extends BaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TravelCourseRepository travelCourseRepository;

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
        country = countryRepository.save(CountryFixture.getMockCountry());
        region = regionRepository.save(RegionFixture.getMockRegion_1(country));
        video = videoRepository.save(VideoFixture.getMockVideo_1(LocalDate.now()));
        travelCourse = travelCourseRepository.save(TravelCourseFixture.getMockTravelCourse(video, country, region));
    }

    @Nested
    class GetTravelCourseListAPI {

        @Nested
        @DisplayName("날짜순_조회_동작_테스트")
        class UploadDateFilterTest {
            @Test
            void 여행목록_날짜순_조회_성공하면_200_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", country.getName())
                        .param("regionName", region.getName())
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "uploadDate")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(200);
            }

            @Test
            void 여행목록_날짜순_조회_지역명이_없어도_200_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", country.getName())
                        .param("regionName", "")
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "uploadDate")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(200);
            }

            @Test
            void 여행목록_날짜순_조회_나라명이_null이면_400_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", (Object) null)
                        .param("regionName", region.getName())
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "uploadDate")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(400);
            }

            @Test
            void 여행목록_날짜순_조회_나라명이_비어있으면_400_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", "")
                        .param("regionName", region.getName())
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "uploadDate")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(400);
            }
        }

        @Nested
        @DisplayName("조회순_조회_동작_테스트")
        class ViewCountFilterTest {
            @Test
            void 여행목록_조회순_조회_성공하면_200_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", country.getName())
                        .param("regionName", region.getName())
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "viewCount")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(200);
            }

            @Test
            void 여행목록_조회순_조회_지역명이_없어도_200_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", country.getName())
                        .param("regionName", "")
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "viewCount")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(200);
            }

            @Test
            void 여행목록_조회순_조회_나라명이_null이면_400_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", (Object) null)
                        .param("regionName", region.getName())
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "viewCount")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(400);
            }

            @Test
            void 여행목록_조회순_조회_나라명이_비어있으면_400_응답을_보낸다() {
                RestAssured.given()
                        .param("countryName", "")
                        .param("regionName", region.getName())
                        .param("pageNumber", 0)
                        .param("pageSize", 8)
                        .param("sortField", "viewCount")
                        .when()
                        .get("/api/v1/travelcourses")
                        .then()
                        .statusCode(400);
            }
        }
    }

    @Nested
    class GetTravelCourseDetailAPI {

        @Test
        void 여행_상세_조회_성공하면_200_응답을_보낸다() {
            RestAssured.given()
                    .pathParam("id", travelCourse.getId())
                    .when()
                    .get("/api/v1/travelcourses/{id}")
                    .then()
                    .statusCode(200);
        }

        @Test
        void 여행_상세_조회_id가_없으면_404_응답을_보낸다() {
            RestAssured.given()
                    .when()
                    .get("/api/v1/travelcourses/") // id 없이 호출
                    .then()
                    .statusCode(404);
        }
    }
}
