package org.greedy.ddarahang.common.exception;

import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.greedy.ddarahang.api.service.TravelCourseService;
import org.greedy.ddarahang.common.AllFixture;
import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.place.PlaceRepository;
import org.greedy.ddarahang.db.region.Region;
import org.greedy.ddarahang.db.region.RegionRepository;
import org.greedy.ddarahang.db.travelCourse.TravelCourse;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetail;
import org.greedy.ddarahang.db.travelCourseDetail.TravelCourseDetailRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalExceptionHandlerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TravelCourseRepository travelCourseRepository;

    @Autowired
    private TravelCourseDetailRepository travelCourseDetailRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country country;
    private Region region;
    private Place place;
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
        placeRepository.deleteAll();
        travelCourseDetailRepository.deleteAll();

        prepareTestData();
    }

    private void prepareTestData() {
        country = countryRepository.save(AllFixture.getMockCountry());
        region = regionRepository.save(AllFixture.getMockRegion(country));
        place = placeRepository.save(AllFixture.getMockPlace(region));
        video = videoRepository.save(AllFixture.getMockVideo(LocalDate.now()));
        travelCourse = travelCourseRepository.save(AllFixture.getMockTravelCourse(video, country, region));
        travelCourseDetail = travelCourseDetailRepository.save(AllFixture.getMockTravelCourseDetail(travelCourse,place));
    }

    /**
     * 🔹 Exception 발생 테스트
     */
    @Nested
    @Transactional
    class 예외발생테스트{

        @Test
        void 해당_id를_가진_데이터가_없으면_NotFoundTravelCourseDetailException_발생() {
            TravelCourseService service = new TravelCourseService(travelCourseRepository, travelCourseDetailRepository);

            assertThrows(NotFoundTravelCourseDetailException.class, () -> service.getTravelCourseDetail(-1L));
        }

        @Test
        void 나라명이_null이면_InvalidCountryNameException_발생() {
            TravelCourseService service = new TravelCourseService(travelCourseRepository, travelCourseDetailRepository);

            assertThrows(InvalidCountryNameException.class, () -> service.getTravelCourses(null, region.getName()));
        }

        @Test
        void 여행_상세_조회_아이디가_null이면_MissingIdException_발생() {
            TravelCourseService service = new TravelCourseService(travelCourseRepository, travelCourseDetailRepository);

            assertThrows(MissingIdException.class, () -> service.getTravelCourseDetail(null));
        }
    }
}
