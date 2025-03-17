package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.common.exception.DataSyncException;
import org.greedy.ddarahang.db.country.LocationType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelDataSyncService {

    private final GoogleSheetService googleSheetService;
    private final JdbcTemplate jdbcTemplate;
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final Map<String, String> sheetEndCellMap = new HashMap<>() {{
        put("Country", "C");
        put("Region", "C");
        put("Place", "G");
        put("Video", "G");
        put("TravelCourse", "E");
        put("TravelCourseDetail", "E");
    }};

    @Transactional
    public void syncGoogleSheetWithDB() throws GeneralSecurityException, IOException {

        syncData("Country", "countries", "INSERT INTO countries (name, location_type) VALUES (?, ?)",
                (ps, row) -> {
                    try {
                        String countryName = row.get(1).toString().trim();
                        String locationType = LocationType.getLocationType(countryName).toString();

                        ps.setString(1, countryName);
                        ps.setString(2, locationType);

                        log.info("Country 데이터 준비 - Name: {}, LocationType: {}", countryName, locationType);
                    } catch (SQLException e) {
                        log.error("Country 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into countries");
                    }
                });

        syncData("Region", "regions", "INSERT INTO regions (name, country_id) VALUES (?, ?)",
                (ps, row) -> {
                    try {
                        String regionName = row.get(1).toString().trim();
                        long countryId = Long.parseLong(row.get(2).toString().trim());

                        ps.setString(1, regionName);
                        ps.setLong(2, countryId);

                        log.info("Region 삽입 - Name: {}, CountryID: {}", regionName, countryId);
                    } catch (SQLException e) {
                        log.error("Region 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into regions");
                    }
                });

        syncData("Place", "places", "INSERT INTO places (name, address, latitude, longitude, region_id, tag) VALUES (?, ?, ?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        String placeName = row.get(1).toString().trim();
                        String address = row.get(2).toString().trim();
                        double latitude = Double.parseDouble(row.get(3).toString().trim());
                        double longitude = Double.parseDouble(row.get(4).toString().trim());
                        long regionId = Long.parseLong(row.get(5).toString().trim());
                        String tag = row.get(6).toString().trim();

                        ps.setString(1, placeName);
                        ps.setString(2, address);
                        ps.setDouble(3, latitude);
                        ps.setDouble(4, longitude);
                        ps.setLong(5, regionId);
                        ps.setString(6, tag);

                        log.info("Place 삽입 - Name: {}, Address: {}", placeName, address);

                    } catch (SQLException e) {
                        log.error("Place 데이터 삽입 실패: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into places");
                    }
                });

        syncData("Video", "videos", "INSERT INTO videos (video_url, thumbnail_url, title, view_count, creator, upload_date) VALUES (?, ?, ?, ?, ?, ?)",
                (ps, row) -> {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        String videoUrl = row.get(1).toString().trim();
                        String thumbnailUrl = row.get(2).toString().trim();
                        String title = row.get(3).toString().trim();
                        long viewCount = Long.parseLong(row.get(4).toString().trim());
                        String creator = row.get(5).toString().trim();
                        LocalDate uploadDate = LocalDate.parse(row.get(6).toString().trim(), dateFormatter);

                        ps.setString(1, videoUrl);
                        ps.setString(2, thumbnailUrl);
                        ps.setString(3, title);
                        ps.setLong(4, viewCount);
                        ps.setString(5, creator);
                        ps.setObject(6, uploadDate);

                        log.info("Video 삽입 - URL: {} Title: {} ViewCount: {} Creator: {} UploadDate: {}",
                                videoUrl, title, viewCount, creator, uploadDate);
                    } catch (SQLException | DateTimeParseException e) {
                        log.error("Video 데이터 삽입 실패 - 오류: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into videos");
                    }
                });

        syncData("TravelCourse", "travel_courses", "INSERT INTO travel_courses (video_id, travel_days, country_id, region_id) VALUES (?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        long videoId = Long.parseLong(row.get(1).toString().trim());
                        int travelDays = Integer.parseInt(row.get(2).toString().trim());
                        long countryId = Long.parseLong(row.get(3).toString().trim());
                        long regionId = Long.parseLong(row.get(4).toString().trim());

                        ps.setLong(1, videoId);
                        ps.setInt(2, travelDays);
                        ps.setLong(3, countryId);
                        ps.setLong(4, regionId);

                        log.info("TravelCourse 삽입 - VideoID: {}, TravelDays: {}, CountryID: {}, RegionID: {}",
                                videoId, travelDays, countryId, regionId);

                    } catch (SQLException e) {
                        log.error("TravelCourse 데이터 삽입 실패 - 오류: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into travel_courses");
                    }
                });

        syncData("TravelCourseDetail", "travel_course_details", "INSERT INTO travel_course_details (travel_course_id, day, order_in_day, place_id) VALUES (?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        long travelCourseId = Long.parseLong(row.get(1).toString().trim());
                        int day = Integer.parseInt(row.get(2).toString().trim());
                        int orderInDay = Integer.parseInt(row.get(3).toString().trim());
                        long placeId = Long.parseLong(row.get(4).toString().trim());

                        ps.setLong(1, travelCourseId);
                        ps.setInt(2, day);
                        ps.setInt(3, orderInDay);
                        ps.setLong(4, placeId);

                        log.info("TravelCourseDetail 삽입 - travelCourseId: {}, day: {}, orderInDay: {}, placeId: {}",
                                travelCourseId, day, orderInDay, placeId);
                    } catch (SQLException e) {
                        log.error("TravelCourseDetail 데이터 삽입 실패 - 오류: {}", e.getMessage());
                        throw new DataSyncException("Failed to insert data into travel_course_details");
                    }
                });
    }

    private void syncData(String sheetName, String tableName, String sql, BiConsumer<PreparedStatement, List<Object>> setter) {
        try {
            Long lastProcessedRow = getLastProcessedRow(tableName);
            String endCell = sheetEndCellMap.get(sheetName);
            String startCell = "A" + (lastProcessedRow + 1);
            String range = startCell + ":" + endCell;

            List<List<Object>> data = googleSheetService.getSheetData(sheetName, range);

            if (data == null || data.isEmpty()) {
                log.info(" 삽입할 데이터가 없습니다.");
                return;
            }

            batchInsert(data, sql, setter);
            updateLastProcessedRow(tableName, lastProcessedRow + data.size());

        } catch (IOException e) {
            log.error(sheetName + " 시트 동기화 중 Google Sheets API 호출 실패", e);
            throw new DataSyncException(sheetName + " 시트 동기화 중 Google API 오류" + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error(sheetName + " 시트 동기화 중 DB 무결성 오류", e);
            throw new DataSyncException(sheetName + " 시트 데이터 저장 중 DB 오류 발생" + e.getMessage());
        } catch (Exception e) {
            log.error(sheetName + " 시트 동기화 중 알 수 없는 오류 발생", e);
            throw new DataSyncException(sheetName + " 시트 데이터 동기화 실패" + e.getMessage());
        }
    }

    private Long getLastProcessedRow(String tableName) {
        try {
            return jdbcTemplate.queryForObject("SELECT last_row FROM sync_status WHERE sheet_name = ?", Long.class, tableName);
        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO sync_status (sheet_name, last_row) VALUES (?, ?)", tableName, 1L);
            return 1L;
        }
    }

    private void updateLastProcessedRow(String tableName, Long lastRow) {
        try {
            jdbcTemplate.update("UPDATE sync_status SET last_row = ? WHERE sheet_name = ?", lastRow, tableName);
        } catch (Exception e) {
            throw new DataSyncException("Failed to update last processed row for table: " + tableName);
        }
    }

    private void batchInsert(List<List<Object>> data, String sql, BiConsumer<PreparedStatement, List<Object>> setter) {
        try {
            for (int i = 0; i < data.size(); i += DEFAULT_BATCH_SIZE) {
                List<List<Object>> batch = data.subList(i, Math.min(i + DEFAULT_BATCH_SIZE, data.size()));
                jdbcTemplate.batchUpdate(sql, batch, batch.size(), setter::accept);
            }
        } catch (Exception e) {
            throw new DataSyncException("Batch insert failed");
        }
    }
}
