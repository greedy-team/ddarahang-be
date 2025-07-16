package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @EntityGraph(attributePaths = {"video", "country", "region", })
    Optional<TravelCourse> findById(Long id);

//    @EntityGraph(attributePaths = {"video", "country", "region"})
//    Page<TravelCourse> findTravelCoursesByCountryName(String countryName, Pageable pageable);
//
//    @EntityGraph(attributePaths = {"video", "country", "region"})
//    Page<TravelCourse> findTravelCoursesByRegionName(String regionName, Pageable pageable);

    @Query("SELECT tc FROM TravelCourse tc " +
            "JOIN FETCH tc.video v " +    // N+1 방지를 위해 video 엔티티를 바로 가져옴
            "JOIN tc.country c " +        // Country 엔티티 조인 (c.name 필터링 위함)
            "LEFT JOIN tc.region r " +    // Region 엔티티 조인 (혹시 모를 상황 대비 LEFT JOIN 유지)
            "WHERE c.name = :countryName " // countryName 조건
        )  // viewCount 정렬
    //@EntityGraph(attributePaths = {"video", "country", "region", })
    Page<TravelCourse> findTravelCoursesByCountryName(String countryName, Pageable pageable);


    // 2. RegionName이 존재하고, countryName도 함께 필터링하는 경우의 쿼리
    // (`request.regionName().isBlank()`가 false일 때 호출됨)
    @Query("SELECT tc FROM TravelCourse tc " +
            "JOIN FETCH tc.video v " +    // N+1 방지를 위해 video 엔티티를 바로 가져옴
            "JOIN tc.country c " +        // Country 엔티티 조인 (c.name 필터링 위함)
            "JOIN tc.region r " +         // Region 엔티티 조인 (r.name 필터링 위함)
            "WHERE r.name = :regionName AND c.name = :countryName " // regionName과 countryName 두 조건 모두 사용
            )  // viewCount 정렬
    //@EntityGraph(attributePaths = {"video", "country", "region", })
    Page<TravelCourse> findByRegionNameAndCountryName(String regionName, String countryName, Pageable pageable);

    @Modifying
    @Query("UPDATE TravelCourse tc SET tc.videoViewCount = :viewCount WHERE tc.video.id = :videoId")
    void updateVideoViewCountByVideoId(Long videoId, String viewCount);

}
