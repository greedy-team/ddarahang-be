package org.greedy.ddarahang.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.CountryRepository;
import org.greedy.ddarahang.db.country.LocationType;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestDataService {

    private final TravelCourseRepository travelCourseRepository;
    private final TravelCourseDetailRepository travelCourseDetailRepository;
    private final PlaceRepository placeRepository;
    private final VideoRepository videoRepository;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final Faker faker = new Faker(Locale.KOREA);

    @Async
    @Transactional
    public void generateTestData() {
        try {
            log.info("Starting test data generation...");

            // 순서 중요: Country -> Region -> Place, Video (독립적) -> TravelCourse -> TravelCourseDetail
            List<Country> countries = createSampleCountries(10); // 예시: 10개 국가 생성
            List<Region> regions = createSampleRegions(50, countries); // 예시: 50개 지역 생성
            List<Place> places = createSamplePlaces(100, regions); // Place에 Region 할당 추가
            List<Video> videos = createSampleVideos(50); // 예시: 50개 비디오 생성

            createTravelCourses(1_000_000, places, videos, countries, regions);

            log.info("Test data generation completed successfully!");
        } catch (Exception e) {
            log.error("Error generating test data", e);
            throw new RuntimeException("Failed to generate test data", e);
        }
    }

    private List<Country> createSampleCountries(int count) {
        log.info("Creating {} sample countries...", count);
        List<Country> countries = new ArrayList<>();
        // LocationType은 Enum이므로, 미리 정의된 값 중 하나를 선택해야 함
        LocationType[] locationTypes = LocationType.values();

        for (int i = 0; i < count; i++) {
            Country country = Country.builder()
                    .name(faker.address().country() + " " + i) // 이름 중복 방지를 위해 i 추가
                    .locationType(locationTypes[faker.random().nextInt(locationTypes.length)])
                    .build();
            countries.add(country);
        }
        return countryRepository.saveAll(countries);
    }

    private List<Region> createSampleRegions(int count, List<Country> countries) {
        log.info("Creating {} sample regions...", count);
        List<Region> regions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Country randomCountry = countries.get(faker.random().nextInt(countries.size()));
            Region region = Region.builder()
                    .name(faker.address().state() + " " + i) // 이름 중복 방지를 위해 i 추가
                    .country(randomCountry)
                    .build();
            regions.add(region);
        }
        return regionRepository.saveAll(regions);
    }

    private List<Place> createSamplePlaces(int count, List<Region> regions) { // Region 인자 추가
        log.info("Creating {} sample places...", count);
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Region randomRegion = regions.get(faker.random().nextInt(regions.size())); // 랜덤 Region 선택
            Place place = Place.builder()
                    .name(faker.address().cityName() + " " + faker.address().streetName())
                    .address(faker.address().fullAddress())
                    .tag(faker.lorem().word()) // tag 필드 추가
                    .latitude(Double.parseDouble(faker.address().latitude()))
                    .longitude(Double.parseDouble(faker.address().longitude()))

                    .region(randomRegion) // Region 할당
                    .build();
            places.add(place);
        }
        return placeRepository.saveAll(places);
    }

    private List<Video> createSampleVideos(int count) {
        log.info("Creating {} sample videos...", count);
        List<Video> videos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Faker로 날짜 생성
            Date pastDate = faker.date().past(365 * 5, TimeUnit.DAYS); // 5년 전부터 현재까지
            LocalDate uploadDate = pastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Video video = Video.builder()
                    .title(faker.lorem().sentence(3))
                    .videoUrl(faker.internet().url() + "/video_" + i + ".mp4") // videoUrl
                    .thumbnailUrl(faker.internet().url() + "/thumbnail_" + i + ".jpg") // thumbnailUrl
                    .viewCount(faker.number().numberBetween(100L, 1_000_000L)) // viewCount
                    .creator(faker.name().fullName()) // creator
                    .uploadDate(uploadDate) // uploadDate
                    .build();
            videos.add(video);
        }
        return videoRepository.saveAll(videos);
    }

    private void createTravelCourses(int count, List<Place> places, List<Video> videos, List<Country> countries, List<Region> regions) {
        log.info("Creating {} travel courses...", count);
        int batchSize = 10_000;

        for (int i = 0; i < count; i += batchSize) {
            int currentBatchSize = Math.min(batchSize, count - i);
            List<TravelCourse> batch = new ArrayList<>(currentBatchSize);

            for (int j = 0; j < currentBatchSize; j++) {
                long currentCourseId = (long) i + j + 1; // 1부터 시작하는 ID 할당

                Video randomVideo = videos.get(faker.random().nextInt(videos.size()));
                Country randomCountry = countries.get(faker.random().nextInt(countries.size()));
                Region randomRegion = regions.get(faker.random().nextInt(regions.size()));

                TravelCourse course = TravelCourse.builder()
                        .id(currentCourseId)
                        .travelDays(faker.number().numberBetween(1, 10))
                        .video(randomVideo)
                        .country(randomCountry)
                        .region(randomRegion)
                        .build();
                batch.add(course);
            }

            List<TravelCourse> savedCourses = travelCourseRepository.saveAll(batch);
            createTravelCourseDetails(savedCourses, places);

            log.info("Created {} travel courses ({}% complete)",
                    i + currentBatchSize,
                    ((long)(i + currentBatchSize) * 100) / count);
        }
    }

    private void createTravelCourseDetails(List<TravelCourse> courses, List<Place> places) {
        List<TravelCourseDetail> details = new ArrayList<>();

        for (TravelCourse course : courses) {
            if (course.getId() == 1L) {
                for (int j = 1; j <= 20; j++) {
                    details.add(createDetail(course, j, places));
                }
            } else {
                details.add(createDetail(course, 1, places));
            }

            if (details.size() >= 10_000) {
                travelCourseDetailRepository.saveAll(details);
                details.clear();
            }
        }

        if (!details.isEmpty()) {
            travelCourseDetailRepository.saveAll(details);
        }
    }

    private TravelCourseDetail createDetail(TravelCourse course, int order, List<Place> places) {
        Place randomPlace = places.get(faker.random().nextInt(places.size()));

        return TravelCourseDetail.builder()
                .travelCourse(course)
                .place(randomPlace)
                .day(faker.number().numberBetween(1, course.getTravelDays()))
                .orderInDay(order)
                .build();
    }

    // TestDataService.java (기존 코드에 다음 메서드들을 추가하세요)

    // --- 새로운 인기 여행지 테스트 데이터 생성 메서드 (view_count 정렬 테스트용) ---
    @Async
    @Transactional
    public void generatePopularCoursesTestData() {
        try {
            log.info("Starting popular courses test data generation...");

            // **1. 특정 Country ("TestCountry") 생성 또는 조회**
            String testCountryName = "TestCountry";
            Country targetCountry = countryRepository.findByName(testCountryName)
                    .orElseGet(() -> { // 없으면 새로 생성
                        log.warn("Test Country '{}' not found. Creating it.", testCountryName);
                        return countryRepository.save(Country.builder()
                                .name(testCountryName)
                                .locationType(LocationType.DOMESTIC) // 아무 LocationType 지정
                                .build());
                    });

            // **2. 특정 Region ("TestRegion") 생성 또는 조회 (위에서 생성한 Country에 연결)**
            String testRegionName = "TestRegion";
            Region targetRegion = regionRepository.findByName(testRegionName)
                    .orElseGet(() -> { // 없으면 새로 생성
                        log.warn("Test Region '{}' not found. Creating it.", testRegionName);
                        return regionRepository.save(Region.builder()
                                .name(testRegionName)
                                .country(targetCountry) // 특정 Country에 연결
                                .build());
                    });

            // **3. 특정 Region에 속하는 Places 생성 (선택 사항이지만 일관성을 위해)**
            // 기존 createSamplePlaces는 랜덤 Region에 할당하므로, 특정 Region에 할당하는 새 메서드 사용
            List<Place> targetPlaces = createSamplePlacesForSpecificRegion(100, targetRegion);


            // 4. 1,000,000개의 Video 레코드 생성
            List<Video> newVideos = createOneMillionVideosForPopularityTest(1_000_000);

            // **5. 1,000,000개의 TravelCourse 레코드 생성 (특정 Country/Region/Video에 연결)**
            createOneMillionTravelCoursesForSpecificLocation(1_000_000, newVideos, targetCountry, targetRegion);

            log.info("Popular courses test data generation completed successfully!");
        } catch (Exception e) {
            log.error("Error generating popular courses test data", e);
            throw new RuntimeException("Failed to generate popular courses test data", e);
        }
    }


    // --- 새로운 헬퍼 메서드 (특정 Country/Region에 데이터 삽입용) ---

    // 특정 Region에 속하는 Place를 생성하는 헬퍼 메서드
    private List<Place> createSamplePlacesForSpecificRegion(int count, Region region) {
        log.info("Creating {} sample places for specific region '{}'...", count, region.getName());
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Place place = Place.builder()
                    .name(faker.address().cityName() + " " + faker.address().streetName() + " (" + region.getName() + " Place " + i + ")")
                    .address(faker.address().fullAddress())
                    .tag(faker.lorem().word())
                    .latitude(Double.parseDouble(faker.address().latitude()))
                    .longitude(Double.parseDouble(faker.address().longitude()))
                    .region(region) // 특정 Region 할당
                    .build();
            places.add(place);
        }
        return placeRepository.saveAll(places);
    }


    // 100만개 비디오 생성 (뷰카운트 범위 넓게) - 이 메서드는 이전과 동일
    private List<Video> createOneMillionVideosForPopularityTest(int count) {
        log.info("Creating {} videos for popular courses test...", count);
        List<Video> videos = new ArrayList<>();
        int batchSize = 10_000;
        long lastLoggedPercentage = -1;

        for (int i = 0; i < count; i += batchSize) {
            List<Video> batch = new ArrayList<>(Math.min(batchSize, count - i));
            for (int j = 0; j < Math.min(batchSize, count - i); j++) {
                Date pastDate = faker.date().past(365 * 5, TimeUnit.DAYS);
                LocalDate uploadDate = pastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                Video video = Video.builder()
                        .title(faker.lorem().sentence(3))
                        .videoUrl(faker.internet().url() + "/video_pop_" + (i + j) + ".mp4")
                        .thumbnailUrl(faker.internet().url() + "/thumbnail_pop_" + (i + j) + ".jpg")
                        .viewCount(faker.number().numberBetween(100L, 100_000_000L))
                        .creator(faker.name().fullName())
                        .uploadDate(uploadDate)
                        .build();
                batch.add(video);
            }
            videos.addAll(videoRepository.saveAll(batch));

            long currentPercentage = ((long)(i + batch.size()) * 100) / count;
            if (currentPercentage % 10 == 0 && currentPercentage != lastLoggedPercentage) {
                log.info("Video data generation progress: {}%", currentPercentage);
                lastLoggedPercentage = currentPercentage;
            }
        }
        if (lastLoggedPercentage != 100) {
            log.info("Video data generation progress: 100%");
        }
        return videos;
    }

    // 100만개 TravelCourse 생성 (특정 Country/Region/Video에 연결)
    private void createOneMillionTravelCoursesForSpecificLocation(int count, List<Video> videos, Country country, Region region) {
        log.info("Creating {} travel courses for specific location '{}' / '{}'...", count, country.getName(), region.getName());
        if (videos.size() < count) {
            throw new IllegalStateException("Not enough videos generated to link with " + count + " travel courses.");
        }
        int batchSize = 10_000;
        long startCourseId = 1_000_000 + 1; // 기존 TravelCourse ID와 겹치지 않도록

        long lastLoggedPercentage = -1;

        for (int i = 0; i < count; i += batchSize) {
            List<TravelCourse> batch = new ArrayList<>(Math.min(batchSize, count - i));
            for (int j = 0; j < Math.min(batchSize, count - i); j++) {
                long currentCourseId = startCourseId + i + j;

                Video linkedVideo = videos.get(i + j);
                // Country와 Region을 랜덤으로 선택하는 대신, 특정 Country/Region 객체 사용
                // Country randomCountry = countries.get(faker.random().nextInt(countries.size()));
                // Region randomRegion = regions.get(faker.random().nextInt(regions.size()));

                TravelCourse course = TravelCourse.builder()
                        .id(currentCourseId)
                        .travelDays(faker.number().numberBetween(1, 10))
                        .video(linkedVideo)
                        .country(country) // 특정 Country 객체 할당
                        .region(region)   // 특정 Region 객체 할당
                        .build();
                batch.add(course);
            }
            travelCourseRepository.saveAll(batch);

            long currentPercentage = ((long)(i + batch.size()) * 100) / count;
            if (currentPercentage % 10 == 0 && currentPercentage != lastLoggedPercentage) {
                log.info("Travel Course data generation progress: {}%", currentPercentage);
                lastLoggedPercentage = currentPercentage;
            }
        }
        if (lastLoggedPercentage != 100) {
            log.info("Travel Course data generation progress: 100%");
        }
    }
}