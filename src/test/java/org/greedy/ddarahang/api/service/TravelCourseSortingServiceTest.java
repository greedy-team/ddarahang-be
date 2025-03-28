//package org.greedy.ddarahang.api.service;
//
//import org.greedy.ddarahang.api.dto.TravelCourseListResponse;
//import org.greedy.ddarahang.common.fixture.CountryFixture;
//import org.greedy.ddarahang.common.fixture.RegionFixture;
//import org.greedy.ddarahang.common.fixture.TravelCourseFixture;
//import org.greedy.ddarahang.common.fixture.VideoFixture;
//import org.greedy.ddarahang.db.country.Country;
//import org.greedy.ddarahang.db.region.Region;
//import org.greedy.ddarahang.db.travelCourse.TravelCourse;
//import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
//import org.greedy.ddarahang.db.video.Video;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//public class TravelCourseSortingServiceTest {
//
//    @InjectMocks
//    private TravelCourseService travelCourseService;
//
//    @Mock
//    private TravelCourseRepository travelCourseRepository;
//
//    private Country mockCountry;
//    private Region mockRegion;
//    private List<TravelCourse> travelCourses;
//
//    @BeforeEach
//    void setUp() {
//        Video mockVideo1 = VideoFixture.getMockVideo_1(LocalDate.now());
//        Video mockVideo2 = VideoFixture.getMockVideo_2(LocalDate.now());
//
//        this.mockCountry = CountryFixture.getMockCountry();
//        this.mockRegion = RegionFixture.getMockRegion_1(this.mockCountry);
//
//        TravelCourse mockTravelCourse1 = TravelCourseFixture.getMockTravelCourse(mockVideo1, mockCountry, mockRegion);
//        TravelCourse mockTravelCourse2 = TravelCourseFixture.getMockTravelCourse(mockVideo2, mockCountry, mockRegion);
//
//        //정렬 전
//        this.travelCourses = List.of(mockTravelCourse1, mockTravelCourse2);
//    }
//
//    @Test
//    void 여행코스_조회수순_정렬_테스트() {
//        //given
//
//        //when
//        List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses("viewCount", mockCountry.getName(), mockRegion.getName());
//
//        //then
//        List<Long> viewCounts = responses.stream()
//                .map(TravelCourseListResponse::viewCount)
//                .collect(Collectors.toList());
//
//        assertThat(viewCounts).isSortedAccordingTo((v1, v2) -> Long.compare(v2, v1));
//    }
//
//    @Test
//    void 여행코스_날짜순_정렬_테스트() {
//        //given
//
//        //when
//        List<TravelCourseListResponse> responses = travelCourseService.getTravelCourses("uploadDate", mockCountry.getName(), mockRegion.getName());
//
//        //then
//        List<LocalDate> uploadDates = responses.stream()
//                .map(TravelCourseListResponse::uploadDate)
//                .collect(Collectors.toList());
//
//        assertThat(uploadDates).isSortedAccordingTo((d1, d2) -> d2.compareTo(d1));
//    }
//}
