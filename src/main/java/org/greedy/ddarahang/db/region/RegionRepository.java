package org.greedy.ddarahang.db.region;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name); // 이름으로 Region 조회 메서드 추가

    @Query("SELECT r.id FROM Region r WHERE r.name = :name")
    Optional<Long> findIdByName(@Param("name") String name);
}
