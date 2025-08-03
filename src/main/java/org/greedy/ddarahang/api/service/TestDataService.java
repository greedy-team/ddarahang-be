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

    /*
     * 데이터 제거 메서드
     */
    @Async
    @Transactional
    public void clearAllTestData() {
        log.warn("!!!! 데이터베이스에서 모든 테스트 데이터를 삭제합니다. 이 작업은 되돌릴 수 없습니다. !!!!");
        try {
            // 삭제 순서: TravelCourseDetail -> TravelCourse -> Video, Place -> Region -> Country
            travelCourseDetailRepository.deleteAllInBatch();
            travelCourseRepository.deleteAllInBatch();
            videoRepository.deleteAllInBatch();
            placeRepository.deleteAllInBatch();
            regionRepository.deleteAllInBatch();
            countryRepository.deleteAllInBatch();
            log.info("모든 테스트 데이터가 성공적으로 삭제되었습니다!");
        } catch (Exception e) {
            log.error("모든 테스트 데이터 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("테스트 데이터 삭제 실패", e);
        }
    }

    /*
     * 1번 Test를 위한 데이터 생성 메서드
     */
    @Async
    @Transactional
    public void generateTest1Data() {
        try {
            log.info("Starting test data generation...");

            List<Country> countries = createSampleCountries(10);
            List<Region> regions = createSampleRegions(50, countries);
            List<Place> places = createSamplePlaces(100, regions);
            List<Video> videos = createSampleVideos(50);

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
        LocationType[] locationTypes = LocationType.values();

        for (int i = 0; i < count; i++) {
            Country country = Country.builder()
                    .name(faker.address().country() + " " + i)
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
                    .name(faker.address().state() + " " + i)
                    .country(randomCountry)
                    .build();
            regions.add(region);
        }
        return regionRepository.saveAll(regions);
    }

    private List<Place> createSamplePlaces(int count, List<Region> regions) {
        log.info("Creating {} sample places...", count);
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Region randomRegion = regions.get(faker.random().nextInt(regions.size()));
            Place place = Place.builder()
                    .name(faker.address().cityName() + " " + faker.address().streetName())
                    .address(faker.address().fullAddress())
                    .tag(faker.lorem().word()) // tag 필드 추가
                    .latitude(Double.parseDouble(faker.address().latitude()))
                    .longitude(Double.parseDouble(faker.address().longitude()))

                    .region(randomRegion)
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
            Date pastDate = faker.date().past(365 * 5, TimeUnit.DAYS);
            LocalDate uploadDate = pastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Video video = Video.builder()
                    .title(faker.lorem().sentence(3))
                    .videoUrl(faker.internet().url() + "/video_" + i + ".mp4")
                    .thumbnailUrl(faker.internet().url() + "/thumbnail_" + i + ".jpg")
                    .viewCount(faker.number().numberBetween(100L, 1_000_000L))
                    .creator(faker.name().fullName())
                    .uploadDate(uploadDate)
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
                long currentCourseId = (long) i + j + 1;

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
                    ((long) (i + currentBatchSize) * 100) / count);
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


    /*
     * 2번 Test를 위한 데이터 생성 메서드
     */
    @Async
    @Transactional
    public void generateTest2SortData() {
        try {
            log.info("Starting popular courses test data generation...");

            String testCountryName = "TestCountry";
            Country targetCountry = countryRepository.findByName(testCountryName)
                    .orElseGet(() -> { // 없으면 새로 생성
                        log.warn("Test Country '{}' not found. Creating it.", testCountryName);
                        return countryRepository.save(Country.builder()
                                .name(testCountryName)
                                .locationType(LocationType.DOMESTIC)
                                .build());
                    });

            String testRegionName = "TestRegion";
            Region targetRegion = regionRepository.findByName(testRegionName)
                    .orElseGet(() -> {
                        log.warn("Test Region '{}' not found. Creating it.", testRegionName);
                        return regionRepository.save(Region.builder()
                                .name(testRegionName)
                                .country(targetCountry)
                                .build());
                    });

            List<Place> targetPlaces = createSamplePlacesForSpecificRegion(100, targetRegion);
            List<Video> newVideos = createOneMillionVideosForPopularityTest(1_000_000);

            createOneMillionTravelCoursesForSpecificLocation(1_000_000, newVideos, targetCountry, targetRegion);

            log.info("Popular courses test data generation completed successfully!");
        } catch (Exception e) {
            log.error("Error generating popular courses test data", e);
            throw new RuntimeException("Failed to generate popular courses test data", e);
        }
    }

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
                    .region(region)
                    .build();
            places.add(place);
        }
        return placeRepository.saveAll(places);
    }

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

            long currentPercentage = ((long) (i + batch.size()) * 100) / count;
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

    private void createOneMillionTravelCoursesForSpecificLocation(int count, List<Video> videos, Country country, Region region) {
        log.info("Creating {} travel courses for specific location '{}' / '{}'...", count, country.getName(), region.getName());
        if (videos.size() < count) {
            throw new IllegalStateException("Not enough videos generated to link with " + count + " travel courses.");
        }
        int batchSize = 10_000;
        long startCourseId = 1_000_000 + 1;

        long lastLoggedPercentage = -1;

        for (int i = 0; i < count; i += batchSize) {
            List<TravelCourse> batch = new ArrayList<>(Math.min(batchSize, count - i));
            for (int j = 0; j < Math.min(batchSize, count - i); j++) {
                long currentCourseId = startCourseId + i + j;

                Video linkedVideo = videos.get(i + j);

                TravelCourse course = TravelCourse.builder()
                        .id(currentCourseId)
                        .travelDays(faker.number().numberBetween(1, 10))
                        .video(linkedVideo)
                        .country(country)
                        .region(region)
                        .videoViewCount("0")
                        .build();
                batch.add(course);
            }
            travelCourseRepository.saveAll(batch);

            long currentPercentage = ((long) (i + batch.size()) * 100) / count;
            if (currentPercentage % 10 == 0 && currentPercentage != lastLoggedPercentage) {
                log.info("Travel Course data generation progress: {}%", currentPercentage);
                lastLoggedPercentage = currentPercentage;
            }
        }
        if (lastLoggedPercentage != 100) {
            log.info("Travel Course data generation progress: 100%");
        }
    }

    /*
     * N+1 문제 테스트를 위한 데이터 생성
     */
    private static final int BATCH_SIZE = 10_000;

    @Async
    @Transactional
    public void generateMinimalCoreTestDataForNplus1(int totalCourses) {
        try {
            log.info("N+1 테스트를 위한 최소 데이터 생성을 시작합니다.");
            log.info("  -> TravelCourse 및 Video: {}개", totalCourses);
            log.info("  -> Country: 10개, Region: 50개, Place: 100개 (TravelCourseDetail 생성 제외)");

            List<Country> countries = createCountries(10);
            log.info("Country {}개 생성 완료.", countries.size());

            List<Region> regions = createRegions(50, countries);
            log.info("Region {}개 생성 완료.", regions.size());

            List<Place> places = createPlaces(100, regions);
            log.info("Place {}개 생성 완료.", places.size());

            List<Video> videos = createVideos(totalCourses);
            log.info("Video {}개 생성 완료.", videos.size());

            createTravelCoursesWithoutDetails(totalCourses, videos, countries, regions);
            log.info("TravelCourse {}개 생성 완료.", totalCourses);

            log.info("N+1 테스트를 위한 최소 데이터 생성이 성공적으로 완료되었습니다!");
        } catch (Exception e) {
            log.error("N+1 테스트 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("N+1 테스트 데이터 생성 실패", e);
        }
    }

    private List<Country> createCountries(int count) {
        log.info("  - Creating {} sample countries...", count);
        List<Country> countries = new ArrayList<>();
        LocationType[] locationTypes = LocationType.values();

        for (int i = 0; i < count; i++) {
            Country country = Country.builder()
                    .name(faker.address().country() + " " + System.nanoTime() + "_" + i)
                    .locationType(locationTypes[faker.random().nextInt(locationTypes.length)])
                    .build();
            countries.add(country);
        }
        return countryRepository.saveAll(countries);
    }

    private List<Region> createRegions(int count, List<Country> countries) {
        log.info("  - Creating {} sample regions...", count);
        List<Region> regions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Country randomCountry = countries.get(faker.random().nextInt(countries.size()));
            Region region = Region.builder()
                    .name(faker.address().state() + " " + System.nanoTime() + "_" + i)
                    .country(randomCountry)
                    .build();
            regions.add(region);
        }
        return regionRepository.saveAll(regions);
    }

    private List<Place> createPlaces(int count, List<Region> regions) {
        log.info("  - Creating {} sample places...", count);
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Region randomRegion = regions.get(faker.random().nextInt(regions.size()));

            String tagWord = faker.lorem().word();
            String safeTag = (tagWord != null && !tagWord.isEmpty()) ?
                    tagWord.substring(0, Math.min(tagWord.length(), 20)) :
                    "random_tag";

            Place place = Place.builder()
                    .name(faker.address().cityName() + " " + faker.address().streetName())
                    .address(faker.address().fullAddress())
                    .tag(safeTag)
                    .latitude(Double.parseDouble(faker.address().latitude()))
                    .longitude(Double.parseDouble(faker.address().longitude()))
                    .region(randomRegion)
                    .build();
            places.add(place);
        }
        return placeRepository.saveAll(places);
    }

    private List<Video> createVideos(int count) {
        log.info("  - Creating {} videos...", count);
        List<Video> videos = new ArrayList<>();
        long lastLoggedPercentage = -1;

        for (int i = 0; i < count; i += BATCH_SIZE) {
            List<Video> batch = new ArrayList<>(Math.min(BATCH_SIZE, count - i));
            for (int j = 0; j < Math.min(BATCH_SIZE, count - i); j++) {
                Date pastDate = faker.date().past(365 * 5, TimeUnit.DAYS);
                LocalDate uploadDate = pastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                Video video = Video.builder()
                        .title(faker.lorem().sentence(3) + " - Video " + (i + j))
                        .videoUrl(faker.internet().url() + "/video_" + (i + j) + ".mp4")
                        .thumbnailUrl(faker.internet().url() + "/thumbnail_" + (i + j) + ".jpg")
                        .viewCount(faker.number().numberBetween(100L, 1_000_000_000L))
                        .creator(faker.name().fullName())
                        .uploadDate(uploadDate)
                        .build();
                batch.add(video);
            }
            videos.addAll(videoRepository.saveAll(batch));

            long currentPercentage = ((long) (i + batch.size()) * 100) / count;
            if (currentPercentage % 10 == 0 && currentPercentage != lastLoggedPercentage) {
                log.info("  - Video data generation progress: {}%", currentPercentage);
                lastLoggedPercentage = currentPercentage;
            }
        }
        if (lastLoggedPercentage != 100) {
            log.info("  - Video data generation progress: 100%");
        }
        return videos;
    }

    private void createTravelCoursesWithoutDetails(int count, List<Video> videos, List<Country> countries, List<Region> regions) {
        log.info("  - Creating {} travel courses (without details)...", count);
        if (videos.size() < count) {
            throw new IllegalStateException("생성할 TravelCourse 개수에 비해 Video 개수가 부족합니다. (" + videos.size() + "개 중 " + count + "개 요청)");
        }
        long lastLoggedPercentage = -1;

        for (int i = 0; i < count; i += BATCH_SIZE) {
            List<TravelCourse> batch = new ArrayList<>(Math.min(BATCH_SIZE, count - i));
            List<Video> currentVideosBatch = videos.subList(i, Math.min(i + BATCH_SIZE, videos.size()));

            for (int j = 0; j < currentVideosBatch.size(); j++) {
                TravelCourse course = TravelCourse.builder()
                        .travelDays(faker.number().numberBetween(1, 10))
                        .video(currentVideosBatch.get(j))
                        .country(countries.get(faker.random().nextInt(countries.size())))
                        .region(regions.get(faker.random().nextInt(regions.size())))
                        .build();
                batch.add(course);
            }

            travelCourseRepository.saveAll(batch);

            long currentPercentage = ((long) (i + batch.size()) * 100) / count;
            if (currentPercentage % 10 == 0 && currentPercentage != lastLoggedPercentage) {
                log.info("  - Travel Course data generation progress: {}%", currentPercentage);
                lastLoggedPercentage = currentPercentage;
            }
        }
        if (lastLoggedPercentage != 100) {
            log.info("  - Travel Course data generation progress: 100%");
        }
    }

    /*
     * 3번 Test를 위한 데이터 생성 메서드
     */
    @Async
    @Transactional
    public void generateRegionCountryFilterTestData() {
        try {
            log.info("🔍 [필터링 성능 테스트] 유럽 > 크로아티아 TravelCourse 생성 시작");

            // 1. Country 20개 생성 (크로아티아 포함)
            List<Country> countries = createCountries(20);
            Country croatia = countries.stream()
                    .filter(c -> c.getName().toLowerCase().contains("크로아티아"))
                    .findFirst()
                    .orElseGet(() -> {
                        log.info("🇭🇷 '크로아티아' Country 새로 생성");
                        Country c = Country.builder()
                                .name("크로아티아")
                                .locationType(LocationType.INTERNATIONAL)
                                .build();
                        return countryRepository.save(c);
                    });

            List<Region> regions = createRegions(200, countries);
            Region europeRegion = Region.builder()
                    .name("유럽")
                    .country(croatia)
                    .build();
            regionRepository.save(europeRegion);
            List<Country> allCountriesExceptTarget = countryRepository.findAll().stream()
                    .filter(c -> !c.getId().equals(croatia.getId()))
                    .toList();
            List<Region> allRegionsExceptTarget = regionRepository.findAll().stream()
                    .filter(r -> !r.getId().equals(europeRegion.getId()))
                    .toList();
            List<Video> videos = createVideos(1_000_000);

            createFilteredTravelCourses(videos, croatia, europeRegion, allCountriesExceptTarget, allRegionsExceptTarget);

            log.info("✅ 필터링 테스트용 TravelCourse 데이터 생성 완료");
        } catch (Exception e) {
            log.error("❌ 필터링 테스트 데이터 생성 실패", e);
            throw new RuntimeException("Filter Test Data Generation Failed", e);
        }
    }

    private void createFilteredTravelCourses(
            List<Video> videos,
            Country targetCountry,
            Region targetRegion,
            List<Country> allCountriesExceptTarget,
            List<Region> allRegionsExceptTarget
    ) {
        int count = videos.size();
        int batchSize = 10_000;

        for (int i = 0; i < count; i += batchSize) {
            List<TravelCourse> batch = new ArrayList<>();
            for (int j = 0; j < Math.min(batchSize, count - i); j++) {
                Video video = videos.get(i + j);

                boolean isTargetCombo = faker.random().nextInt(100) < 5;
                Country country = isTargetCombo ? targetCountry : getRandomCountry(allCountriesExceptTarget);
                Region region = isTargetCombo ? targetRegion : getRandomRegion(allRegionsExceptTarget);

                TravelCourse course = TravelCourse.builder()
                        .travelDays(faker.number().numberBetween(1, 10))
                        .video(video)
                        .country(country)
                        .region(region)
                        .videoUploadDate(String.valueOf(video.getUploadDate()))
                        .videoViewCount(String.valueOf(video.getViewCount()))
                        .build();

                batch.add(course);
            }
            travelCourseRepository.saveAll(batch);

            long progress = ((long) (i + batchSize) * 100) / count;
            if (progress % 10 == 0) log.info("⏳ TravelCourse 생성 진행률: {}%", progress);
        }
    }

    private Country getRandomCountry(List<Country> candidates) {
        return candidates.get(faker.random().nextInt(candidates.size()));
    }

    private Region getRandomRegion(List<Region> candidates) {
        return candidates.get(faker.random().nextInt(candidates.size()));
    }

}