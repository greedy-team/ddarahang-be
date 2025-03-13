package org.greedy.ddarahang.api.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.common.exception.DataSyncException;
import org.greedy.ddarahang.db.country.LocationType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelDataSyncService {

    private final GoogleSheetService googleSheetService;
    private final JdbcTemplate jdbcTemplate;
    private static final int DEFAULT_BATCH_SIZE = 1000;

    @Transactional
    @PostConstruct
    // @Scheduled(cron = "0 0 0 * * WED")
    public void syncGoogleSheetWithDB() {

        syncData("Main", "countries", "INSERT IGNORE INTO countries (name, location_type) VALUES (?, ?)",
                (ps, row) -> {
                    try {
                        String countryName = row.get(0).toString().trim();
                        String locationType = LocationType.getLocationType(countryName).toString();

                        ps.setString(1, countryName);
                        ps.setString(2, locationType);

                        log.info("📍 Country 데이터 준비 - Name: {}, LocationType: {}", countryName, locationType);

                    } catch (SQLException e) {
                        log.error("❌ Country 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into countries");
                    }
                });

        syncData("Main", "regions", "INSERT IGNORE INTO regions (name, country_id) VALUES (?, ?)",
                (ps, row) -> {
                    try {
                        String regionName = row.get(1).toString().trim();
                        Long countryId = findCountryIdByName(row.get(0).toString().trim());

                        log.info("🏞️ Region 삽입 - Name: {}, CountryID: {}", regionName, countryId);

                        ps.setString(1, regionName);
                        ps.setLong(2, countryId);
                    } catch (SQLException e) {
                        log.error("❌ Region 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into regions");
                    }
                });

        syncData("Main", "videos", "INSERT IGNORE INTO videos (video_url, thumbnail_url, title, view_count, creator, upload_date) VALUES (?, ?, ?, ?, ?, ?)",
                (ps, row) -> {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        String videoUrl = row.get(10).toString().trim();
                        String thumbnailUrl = row.get(11).toString().trim();
                        String title = row.get(12).toString().trim();
                        long viewCount = Long.parseLong(row.get(13).toString().trim().replace(",", ""));
                        String creator = row.get(14).toString().trim();
                        LocalDate uploadDate = LocalDate.parse(row.get(15).toString(), dateFormatter);

                        log.info("Video 삽입 - URL: {} Title: {} ViewCount: {} Creator: {} UploadDate: {}",
                                videoUrl, title, viewCount, creator, uploadDate);

                        ps.setString(1, videoUrl);
                        ps.setString(2, thumbnailUrl);
                        ps.setString(3, title);
                        ps.setLong(4, viewCount);
                        ps.setString(5, creator);
                        ps.setObject(6, uploadDate);
                    } catch (SQLException | DateTimeParseException e) {
                        log.error("❌ Video 데이터 삽입 실패 - 오류: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into videos");
                    }
                });

        syncData("Main", "travel_courses", "INSERT IGNORE INTO travel_courses (video_id, travel_days, country_id, region_id) VALUES (?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        Long videoId = findVideoIdByUrl(row.get(10).toString().trim());
                        Long countryId = findCountryIdByName(row.get(0).toString().trim());
                        Long regionId = findRegionIdByName(row.get(1).toString().trim());

                        log.info("📍 TravelCourse 삽입 - VideoID: {}, TravelDays: {}, CountryID: {}, RegionID: {}",
                                videoId, row.get(10).toString().trim(), countryId, regionId);

                        ps.setInt(1, Integer.parseInt(row.get(9).toString().trim()));
                        ps.setLong(2, videoId);
                        ps.setLong(3, countryId);
                        ps.setLong(4, regionId);

                    } catch (SQLException e) {
                        log.error("❌ TravelCourse 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into travel_courses");
                    }
                });

        syncData("Main", "places",
                "INSERT IGNORE INTO places (name, address, latitude, longitude, region_id) VALUES (?, ?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        String regionName = row.get(1).toString().trim();
                        Long regionId = findRegionIdByName(regionName);
                        Long travelCourseId = findVideoIdByUrl(row.get(10).toString().trim());

                        for (int i = 2; i <= 8; i++) {
                            if (row.size() > i && row.get(i) != null && !row.get(i).toString().isEmpty()) {
                                String data = row.get(i).toString().trim();
                                String[] placeEntries = data.split("//");

                                for (int idx = 0; idx < placeEntries.length; idx++) {
                                    String entry = placeEntries[idx];
                                    String[] parts = entry.split("&&");

                                    if (parts.length == 2) {
                                        String name = parts[0].trim();
                                        String address = parts[1].trim();

                                        // 이것은 좀 고민을 . . .
                                        Double latitude = 0.0;
                                        Double longitude = 0.0;

                                        log.info("📝 SQL 파라미터 값 - Name: {}, Address: {}, Latitude: {}, Longitude: {}, RegionID: {}",
                                                name, address, latitude, longitude, regionId);

                                        Long placeId = findOrCreatePlace(name, address, latitude, longitude, regionId);
                                        insertTravelCourseDetail(travelCourseId, i - 1, idx + 1, placeId);

                                        log.info("📍 Place 삽입 - Name: {}, Address: {}, RegionID: {}", name, address, regionId);

                                        try {
                                            ps.setString(1, name);
                                            ps.setString(2, address);
                                            ps.setDouble(3, latitude);
                                            ps.setDouble(4, longitude);
                                            ps.setLong(5, regionId);
                                        } catch (SQLException e) {
                                            throw new DataSyncException("Failed to insert data into places");
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("❌ Place 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into places");
                    }
                }
        );
    }

    private void syncData(String sheetName, String tableName, String sql, BiConsumer<PreparedStatement, List<Object>> setter) {
        try {
            Long lastProcessedRow = getLastProcessedRow(tableName);
            String startCell = "A" + (lastProcessedRow + 1); // 마지막 행부터 +1 해서 담 데이터 가져오기
            String range = startCell + ":" + 1000; // 이것도 따로 메서드 만들어서 최종 사이즈 가져오려고 함

            log.info("🔄 {} 시트 데이터 가져오기 (Range: {})", sheetName, range);

            List<List<Object>> data = googleSheetService.getSheetData(sheetName, range);

            if (data == null || data.isEmpty()) {
                log.info("⚠️ {} 시트에 처리할 데이터가 없습니다.", sheetName);
                return;
            }

            batchInsert(data, sql, setter);
            updateLastProcessedRow(tableName, lastProcessedRow + data.size());

        } catch (Exception e) {
            log.error("❌ {} 시트 동기화 실패: {}", sheetName, e.getMessage());
            throw new DataSyncException("Failed to sync data for sheet: " + sheetName);
        }
    }

    private Long getLastProcessedRow(String tableName) {
        try {
            return jdbcTemplate.queryForObject("SELECT last_row FROM sync_status WHERE sheet_name = ?", Long.class, tableName);
        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO sync_status (sheet_name, last_row) VALUES (?, ?)", tableName, 1);
            return 1L;
        }
    }

    private void updateLastProcessedRow(String tableName, Long lastRow) {
        try {
            jdbcTemplate.update("UPDATE sync_status SET last_row = ? WHERE sheet_name = ?", lastRow, tableName);
        } catch (Exception e) {
            throw new DataSyncException("Failed to update last processed row: " + tableName);
        }
    }

    private void batchInsert(List<List<Object>> data, String sql, BiConsumer<PreparedStatement, List<Object>> setter) {
        try {
            for (int i = 0; i < data.size(); i += DEFAULT_BATCH_SIZE) {
                List<List<Object>> batch = data.subList(i, Math.min(i + DEFAULT_BATCH_SIZE, data.size()));
                jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, row) -> {
                    try {
                        log.info("📝 SQL 파라미터 값 - Row: {}", row);
                        setter.accept(ps, row);
                    } catch (Exception e) {
                        log.error("❌ SQL 파라미터 바인딩 실패 - Row: {}, 오류: {}", row, e.getMessage());
                        throw new DataSyncException("SQL 파라미터 바인딩 실패");
                    }
                });
            }
            log.info("✅ Batch Insert 성공 - 총 {}건", data.size());
        } catch (Exception e) {
            log.error("❌ Batch Insert 실패 - 오류: {}", e.getMessage());
            throw new DataSyncException("Batch insert failed");
        }
    }

    private Long findVideoIdByUrl(String videoUrl) {
        String sql = "SELECT id FROM videos WHERE video_url = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, videoUrl);
    }

    private Long findCountryIdByName(String countryName) {
        String sql = "SELECT id FROM countries WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, countryName);
    }

    private Long findRegionIdByName(String regionName) {
        String sql = "SELECT id FROM regions WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, regionName);
    }

    private Long findOrCreatePlace(String name, String address, Double latitude, Double longitude, Long regionId) {
        String selectSql = "SELECT id FROM places WHERE name = ? AND address = ?";

        List<Long> placeIds = jdbcTemplate.query(selectSql, new Object[]{name, address}, (rs, rowNum) -> rs.getLong("id"));

        Long placeId = placeIds.isEmpty() ? null : placeIds.get(0);

        if (placeId != null) {
            return placeId;
        } else {
            String insertSql = "INSERT INTO places (name, address, latitude, longitude, region_id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, name, address, latitude, longitude, regionId);

            String selectIdSql = "SELECT LAST_INSERT_ID()";
            placeId = jdbcTemplate.queryForObject(selectIdSql, Long.class);

            return placeId;
        }
    }

    private void insertTravelCourseDetail(Long travelCourseId, int day, int orderInDay, Long placeId) {
        String sql = "INSERT INTO travel_course_details (travel_course_id, day, order_in_day, place_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, travelCourseId, day, orderInDay, placeId);
    }
}
