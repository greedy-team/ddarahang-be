package org.greedy.ddarahang.api.service;

import jakarta.persistence.EntityManagerFactory;
import org.greedy.ddarahang.api.dto.TravelCourseListRequest;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
import org.greedy.ddarahang.api.dto.TravelCourseResponse;
import org.greedy.ddarahang.common.BaseTest;
import org.greedy.ddarahang.common.exception.ErrorMessage;
import org.greedy.ddarahang.common.exception.NotFoundDataException;
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
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class TravelCourseServiceTest extends BaseTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private TravelCourseService travelCourseService;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    TravelCourseRepository travelCourseRepository;

    @Autowired
    TravelCourseDetailRepository travelCourseDetailRepository;

    Country country;
    Region region;
    Place place;
    Place place2;
    Video video;
    Video video2;
    TravelCourse travelCourse;
    TravelCourse travelCourse2;
    TravelCourseDetail travelCourseDetail;
    TravelCourseDetail travelCourseDetail2;

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
        travelCourseDetail = travelCourseDetailRepository.save(TravelCourseDetailFixture.getMockTravelCourseDetail(travelCourse, place));

        place2 = placeRepository.save(PlaceFixture.getMockPlace_2(region));
        video2 = videoRepository.save(VideoFixture.getMockVideo_2(LocalDate.now()));
        travelCourse2 = travelCourseRepository.save(TravelCourseFixture.getMockTravelCourse(video2, country, region));
        travelCourseDetail2 = travelCourseDetailRepository.save(TravelCourseDetailFixture.getMockTravelCourseDetail(travelCourse, place2));
    }

    private Statistics getStatistics() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        return stats;
    }

    @Nested
    class GetTravelCourseListMethod {

        @Nested
        @DisplayName("날짜순_조회_서비스_테스트")
        class uploadDateFilterServiceTest {
            @Test
            void countryName과_regionName이_모두_있으면_같은_지역의_코스가_날짜순으로_정렬된다() {
                // Given
                TravelCourseListRequest request = new TravelCourseListRequest(
                        country.getId(), region.getId(), 0, "uploadDate"
                );

                // When
                Page<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(request);

                // Then
                assertThat(responses.getContent().get(0).uploadDate())
                        .isAfter(responses.getContent().get(1).uploadDate());
            }

            @Test
            void countryName은_있고_regionName이_없어도_같은_나라의_코스가_날짜순으로_정렬된다() {
                // Given
                TravelCourseListRequest request = new TravelCourseListRequest(
                        country.getId(), null, 0, "uploadDate"
                );

                // When
                Page<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(request);

                // Then
                assertThat(responses.getContent().get(0).uploadDate())
                        .isAfter(responses.getContent().get(1).uploadDate());
            }
        }

        @Nested
        @DisplayName("조회순_조회_서비스_테스트")
        class viewCountFilterServiceTest {
            @Test
            void countryName과_regionName이_모두_있으면_같은_지역의_코스가_조회순으로_정렬된다() {
                // Given
                TravelCourseListRequest request = new TravelCourseListRequest(
                        country.getId(), region.getId(), 0, "viewCount"
                );

                // When
                Page<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(request);

                // Then
                assertThat(responses.getContent().get(0).viewCount())
                        .isGreaterThan(responses.getContent().get(1).viewCount());
            }

            @Test
            void countryName은_있고_regionName이_없어도_같은_나라의_코스가_조회순으로_정렬된다() {
                // Given
                TravelCourseListRequest request = new TravelCourseListRequest(
                        country.getId(), null, 0, "viewCount"
                );

                // When
                Page<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(request);

                // Then
                assertThat(responses.getContent().get(0).viewCount())
                        .isGreaterThan(responses.getContent().get(1).viewCount());
            }
        }
    }

    @Nested
    class GetTravelCourseDetailMethod {

        @Test
        void 존재하는_id를_조회하면_정상적으로_반환된다() {
            // When
            TravelCourseResponse response = travelCourseService.getTravelCourseDetail(travelCourse.getId());

            // Then
            assertThat(response.creator()).isEqualTo(travelCourse.getVideo().getCreator());
        }

        @Test
        void 해당_id를_가진_데이터가_없으면_NotFoundTravelCourseDetailException_발생() {
            // When & Then
            assertThatThrownBy(() -> travelCourseService.getTravelCourseDetail(-1L))
                    .isInstanceOf(NotFoundDataException.class)
                    .hasMessage(ErrorMessage.FAILED_TO_INSERT_TRAVEL_COURSE_DETAILS.getMessage());
        }
    }

    @Nested
    class Query_Improvement_Test {

        @Test
        void getTravelCourses_N_plus_1_개선_검증_테스트() {
            // Given
            Statistics stats = getStatistics();

            TravelCourseListRequest request = new TravelCourseListRequest(
                    country.getId(), region.getId(), 0, "uploadDate"
            );

            // When
            travelCourseService.getTravelCourses(request);

            // Then
            long queryCount = stats.getQueryExecutionCount();
            assertEquals(1, queryCount);
        }

        @Test
        void getTravelCourseDetail_N_plus_1_개선_검증_테스트() {
            // Given
            Statistics stats = getStatistics();
            Long id = travelCourse.getId();

            // When
            travelCourseService.getTravelCourseDetail(id);

            // Then
            long queryCount = stats.getQueryExecutionCount();
            assertEquals(1, queryCount);
        }
    }

}
