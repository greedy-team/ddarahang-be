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

    @VideoEntityGraph
    @Query("""
    SELECT tc FROM TravelCourse tc
    JOIN tc.video v
    WHERE (:regionName IS NOT NULL AND :regionName <> '' AND tc.region.name = :regionName)
       OR (:countryName IS NOT NULL AND :regionName = '' AND tc.country.name = :countryName)
    ORDER BY v.viewCount DESC, tc.id DESC
""")
    Page<TravelCourse> findAllByViewCount(
            @Param("countryName") String countryName,
            @Param("regionName") String regionName,
            Pageable pageable
    );

    @VideoEntityGraph
    @Query("""
    SELECT tc FROM TravelCourse tc
    JOIN tc.video v
    WHERE (:regionName IS NOT NULL AND :regionName <> '' AND tc.region.name = :regionName)
       OR (:countryName IS NOT NULL AND :regionName = '' AND tc.country.name = :countryName)
    ORDER BY v.uploadDate DESC, tc.id DESC
""")
    Page<TravelCourse> findAllByUploadDate(
            @Param("countryName") String countryName,
            @Param("regionName") String regionName,
            Pageable pageable
    );

    @VideoEntityGraph
    @Query("""
    SELECT tc FROM TravelCourse tc
    JOIN tc.video v
    WHERE (:regionName IS NOT NULL AND :regionName <> '' AND tc.region.name = :regionName)
       OR (:countryName IS NOT NULL AND :regionName = '' AND tc.country.name = :countryName)
    ORDER BY v.id DESC, tc.id DESC
""")
    Page<TravelCourse> findAllByOrderByIdDesc(
            @Param("countryName") String countryName,
            @Param("regionName") String regionName,
            Pageable pageable
    );
}
