package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.common.exception.DataSyncException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

@Service
@RequiredArgsConstructor
public class TravelDataSyncService {

    private final GoogleSheetService googleSheetService;
    private final JdbcTemplate jdbcTemplate;
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final Map<String, String> sheetEndCellMap = new HashMap<>() {{
        put("Country", "C");
        put("Region", "C");
        put("Place", "F");
        put("Video", "G");
        put("TravelCourse", "E");
        put("TravelCourseDetail", "E");
    }};

    @Transactional
    @Scheduled(cron = "0 0 0 * * TUE")
    public void syncGoogleSheetWithDB() throws GeneralSecurityException, IOException {

        syncData("Country", "countries", "INSERT INTO countries (name, location_type) VALUES (?, ?)",
                (ps, row) -> {
                    try {
                        ps.setString(1, row.get(1).toString().trim());
                        ps.setString(2, row.get(2).toString().trim());
                    } catch (SQLException e) {
                        throw new DataSyncException("Failed to insert data into countries");
                    }
                });

        syncData("Region", "regions", "INSERT INTO regions (name, country_id) VALUES (?, ?)",
                (ps, row) -> {
                    try {
                        ps.setString(1, row.get(1).toString().trim());
                        ps.setLong(2, Long.parseLong(row.get(2).toString().trim()));
                    } catch (SQLException e) {
                        throw new DataSyncException("Failed to insert data into regions");
                    }
                });

        syncData("Place", "places", "INSERT INTO places (name, address, latitude, longitude, region_id) VALUES (?, ?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        ps.setString(1, row.get(1).toString().trim());
                        ps.setString(2, row.get(2).toString().trim());
                        ps.setDouble(3, Double.parseDouble(row.get(3).toString().trim()));
                        ps.setDouble(4, Double.parseDouble(row.get(4).toString().trim()));
                        ps.setLong(5, Long.parseLong(row.get(5).toString().trim()));
                    } catch (SQLException e) {
                        throw new DataSyncException("Failed to insert data into places");
                    }
                });

        syncData("Video", "videos", "INSERT INTO videos (video_url, thumbnail_url, title, view_count, creator, upload_date) VALUES (?, ?, ?, ?, ?, ?)",
                (ps, row) -> {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        ps.setString(1, row.get(1).toString().trim());
                        ps.setString(2, row.get(2).toString().trim());
                        ps.setString(3, row.get(3).toString().trim());
                        ps.setLong(4, Long.parseLong(row.get(4).toString().trim()));
                        ps.setString(5, row.get(5).toString().trim());
                        ps.setObject(6, LocalDate.parse(row.get(6).toString(), dateFormatter));
                    } catch (SQLException | DateTimeParseException e) {
                        throw new DataSyncException("Failed to insert data into videos");
                    }
                });

        syncData("TravelCourse", "travel_courses", "INSERT INTO travel_courses (video_id, travel_days, country_id, region_id) VALUES (?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        ps.setLong(1, Long.parseLong(row.get(1).toString().trim()));
                        ps.setInt(2, Integer.parseInt(row.get(2).toString().trim()));
                        ps.setLong(3, Long.parseLong(row.get(3).toString().trim()));
                        ps.setLong(4, Long.parseLong(row.get(4).toString().trim()));
                    } catch (SQLException e) {
                        throw new DataSyncException("Failed to insert data into travel_courses");
                    }
                });

        syncData("TravelCourseDetail", "travel_course_details", "INSERT INTO travel_course_details (travel_course_id, day, order_in_day, place_id) VALUES (?, ?, ?, ?)",
                (ps, row) -> {
                    try {
                        ps.setLong(1, Long.parseLong(row.get(1).toString().trim()));
                        ps.setInt(2, Integer.parseInt(row.get(2).toString().trim()));
                        ps.setInt(3, Integer.parseInt(row.get(3).toString().trim()));
                        ps.setLong(4, Long.parseLong(row.get(4).toString().trim()));
                    } catch (SQLException e) {
                        throw new DataSyncException("Failed to insert data into travel_course_details");
                    }
                });
    }

    private void syncData(String sheetName, String tableName, String sql, BiConsumer<PreparedStatement, List<Object>> setter) {
        try {
            int lastProcessedRow = getLastProcessedRow(tableName);
            String endCell = sheetEndCellMap.get(sheetName);
            String startCell = "A" + (lastProcessedRow + 1);
            String range = startCell + ":" + endCell;

            List<List<Object>> data = googleSheetService.getSheetData(sheetName, range);

            if (data == null || data.isEmpty()) {
                return;
            }

            batchInsert(data, sql, setter);
            updateLastProcessedRow(tableName, lastProcessedRow + data.size());

        } catch (Exception e) {
            throw new DataSyncException("Failed to sync data for sheet: " + sheetName);
        }
    }

    private int getLastProcessedRow(String tableName) {
        try {
            return jdbcTemplate.queryForObject("SELECT last_row FROM sync_status WHERE sheet_name = ?", Integer.class, tableName);
        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO sync_status (sheet_name, last_row) VALUES (?, ?)", tableName, 1);
            return 1;
        }
    }

    private void updateLastProcessedRow(String tableName, int lastRow) {
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
