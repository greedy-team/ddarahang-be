package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class TravelCourseServiceTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TravelCourseRepository travelCourseRepository;


    @Autowired
    private TravelCourseService travelCourseService;

    private Country country;
    private Region region;
    private Video video;
    private TravelCourse travelCourse;

    @BeforeEach
    void setUp() {
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
     * 🔹 여행_목록_조회
     */
    @Nested
    @Transactional
    class 여행_목록_조회_메서드 {
        @Test
        void regionName이_없는_경우() {
            //Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(country.getName(), region.getName());

            //Then
            assertThat(responses.get(0).creator()).isEqualTo(video.getCreator());
            assertThat(responses.get(0).thumbnailUrl()).isEqualTo(video.getThumbnailUrl());
        }

        @Test
        void regionName이_있는_경우() {
            //Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses(country.getName(), region.getName());

            //Then
            assertThat(responses.get(0).creator()).isEqualTo(video.getCreator());
            assertThat(responses.get(0).thumbnailUrl()).isEqualTo(video.getThumbnailUrl());
        }
    }


    /**
     * 🔹 여행 상세 조회 (`getTravelCourseDetail`)
     */
    @Nested
    @Transactional
    class 여행_상세_조회_메서드 {

        @Test
        void 존재하는_id를_조회하면_정상적으로_반환되며_개수가_일치한다() {
            // When
            TravelCourseResponse response = travelCourseService.getTravelCourseDetail(travelCourse.getId());

            // Then
            assertThat(response.creator()).isEqualTo(travelCourse.getVideo().getCreator());
        }
    }


    /**
     * 🔹 업로드 날짜 내림차순 정렬 (`getSortedByUploadDate`)
     */
    @Nested
    @Transactional
    class 업로드_날짜_내림차순_정렬_메서드 {

        @Test
        void 업로드_날짜가_최신순으로_정렬될때_반환_개수가_일치한다() {
            // Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getSortedByUploadDate(country.getName(), region.getName());

            // Then
            assertThat(responses.size()).isEqualTo(1);
        }

        @Test
        void regionName이_없는_경우에도_정상_조회되며_반환_개수가_일치한다() {
            // Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getSortedByUploadDate(country.getName(), "");

            // Then
            assertThat(responses.size()).isEqualTo(1);
        }
    }

    /**
     * 🔹 조회수 내림차순 정렬 (`getSortedByViewCount`)
     */
    @Nested
    @Transactional
    class 조회수_내림차순_정렬_메서드 {

        @Test
        void 조회수가_높은순으로_정렬될때_반환_개수가_일치한다() {
            // Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getSortedByViewCount(country.getName(), region.getName());

            // Then
            assertThat(responses.size()).isEqualTo(1);
        }

        @Test
        void regionName이_없는_경우에도_정상_조회되며_반환_개수가_일치한다() {
            // Given & When
            List<TravelCourseListResponse> responses = travelCourseService.getSortedByViewCount(country.getName(), "");

            // Then
            assertThat(responses.size()).isEqualTo(1);
        }
    }
}
