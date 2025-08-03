package org.greedy.ddarahang.db.country;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name); // 이름으로 Country 조회 메서드 추가

    @Query("SELECT c.id FROM Country c WHERE c.name = :name")
    Optional<Long> findIdByName(@Param("name") String name);
}
