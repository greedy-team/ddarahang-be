package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelCourseRepository extends JpaRepository<TravelCourse, Long> {

    @VideoEntityGraph
    Optional<TravelCourse> findById(Long id);

    @Query(
            value = """
            SELECT tc.* FROM travel_courses tc
            JOIN videos v ON tc.video_id = v.id
            WHERE (:regionName IS NOT NULL AND :regionName <> '' AND tc.region_id = (SELECT id FROM regions WHERE name = :regionName))
               OR (:countryName IS NOT NULL AND :regionName = '' AND tc.country_id = (SELECT id FROM countries WHERE name = :countryName))
            ORDER BY
                CASE WHEN :sortField = 'viewCount' THEN v.view_count END DESC,
                CASE WHEN :sortField = 'uploadDate' THEN v.upload_date END DESC,
                tc.id DESC
        """,
            countQuery = """
            SELECT COUNT(*) FROM travel_courses tc
            WHERE (:regionName IS NOT NULL AND :regionName <> '' AND tc.region_id = (SELECT id FROM regions WHERE name = :regionName))
               OR (:countryName IS NOT NULL AND :regionName = '' AND tc.country_id = (SELECT id FROM countries WHERE name = :countryName))
        """,
            nativeQuery = true
    )
    Page<TravelCourse> findAllSortedNative(
            @Param("countryName") String countryName,
            @Param("regionName") String regionName,
            @Param("sortField") String sortField,
            Pageable pageable
    );
}
