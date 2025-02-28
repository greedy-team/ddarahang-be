package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.exception.InvalidCountryNameException;
import org.greedy.ddarahang.common.exception.MissingIdException;
import org.greedy.ddarahang.common.exception.NotFoundTravelCourseDetailException;
import org.greedy.ddarahang.common.fixture.CountryFixture;
import org.greedy.ddarahang.common.fixture.PlaceFixture;
import org.greedy.ddarahang.common.fixture.RegionFixture;
import org.greedy.ddarahang.common.fixture.TravelCourseDetailFixture;
import org.greedy.ddarahang.common.fixture.TravelCourseFixture;
import org.greedy.ddarahang.common.fixture.VideoFixture;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
@SuppressWarnings("NonAsciiCharacters")
class TravelCourseServiceTest {

    @Autowired
    private TravelCourseService travelCourseService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TravelCourseRepository travelCourseRepository;

    @Autowired
    private TravelCourseDetailRepository travelCourseDetailRepository;

    private Country country;
    private Region region;
    private Place place;
    private Video video;
    private TravelCourse travelCourse;
    private TravelCourseDetail travelCourseDetail;

    @BeforeEach
    void setUp() {
        travelCourseDetailRepository.deleteAll();
        travelCourseRepository.deleteAll();
        videoRepository.deleteAll();
        placeRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();

        prepareTestData();
    }

    private void prepareTestData() {
        country = countryRepository.save(CountryFixture.getMockCountry());
        region = regionRepository.save(RegionFixture.getMockRegion_1(country));
        place = placeRepository.save(PlaceFixture.getMockPlace_1(region));
        video = videoRepository.save(VideoFixture.getMockVideo_1(LocalDate.now()));
        travelCourse = travelCourseRepository.save(TravelCourseFixture.getMockTravelCourse(video, country, region));
        travelCourseDetail = travelCourseDetailRepository.save(TravelCourseDetailFixture.getMockTravelCourseDetail(travelCourse,place));
    }


    @Nested
    @Transactional
    class c {

        @Test
        void countryName과_regionName이_모두_있으면_데이터가_정상적으로_반환된다() {
            // Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(country.getName(), region.getName());

            // Then
            assertThat(responses.get(0).creator()).isEqualTo(video.getCreator());
            assertThat(responses.get(0).thumbnailUrl()).isEqualTo(video.getThumbnailUrl());
        }

        @Test
        void countryName은_있고_regionName이_없어도_데이터가_정상적으로_반환된다() {
            // Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(country.getName(), "");

            // Then
            assertThat(responses.get(0).creator()).isEqualTo(video.getCreator());
            assertThat(responses.get(0).thumbnailUrl()).isEqualTo(video.getThumbnailUrl());
        }

        @Test
        void countryName이_null이면_InvalidCountryNameException이_터진다() {
            // When & Then
            assertThatThrownBy(() -> travelCourseService.getTravelCourses(null, region.getName()))
                    .isInstanceOf(InvalidCountryNameException.class)
                    .hasMessage("invalid country name");
        }

        @Test
        void countryName이_비어있으면_InvalidCountryNameException이_터진다() {
            // When & Then
            assertThatThrownBy(() -> travelCourseService.getTravelCourses("", region.getName()))
                    .isInstanceOf(InvalidCountryNameException.class)
                    .hasMessage("invalid country name");
        }
    }


    @Nested
    @Transactional
    class d {

        @Test
        void 존재하는_id를_조회하면_정상적으로_반환된다() {
            // When
            TravelCourseResponse response = travelCourseService.getTravelCourseDetail(travelCourse.getId());

            // Then
            assertThat(response.creator()).isEqualTo(travelCourse.getVideo().getCreator());
        }

        @Test
        void 여행_상세_조회_아이디가_null이면_MissingIdException_발생() {
            // When & Then
            assertThatThrownBy(() -> travelCourseService.getTravelCourseDetail(null))
                    .isInstanceOf(MissingIdException.class)
                    .hasMessage("invalid id");
        }

        @Test
        void 해당_id를_가진_데이터가_없으면_NotFoundTravelCourseDetailException_발생() {
            // When & Then
            assertThatThrownBy(() -> travelCourseService.getTravelCourseDetail(-1L))
                    .isInstanceOf(NotFoundTravelCourseDetailException.class)
                    .hasMessage("travel course not found");
        }
    }
}
