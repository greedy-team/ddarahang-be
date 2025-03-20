package org.greedy.ddarahang.api.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.common.config.GoogleSheetsProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private final GoogleSheetsProperties googleSheetsProperties;

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        log.info("Google Sheets 서비스 객체 생성 시작");

        GoogleCredentials credentials;

        try (InputStream serviceAccountStream =
                     new ClassPathResource("ddarahang-local-381179c8ef0e.json").getInputStream()) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        } catch (IOException e) {
            log.warn("클래스패스에서 Google Service Account JSON 로드 실패, 파일 경로 확인 시도");

            String serviceAccountPath = googleSheetsProperties.getServiceAccountKeyPath();
            if (serviceAccountPath == null || serviceAccountPath.isEmpty()) {
                throw new IOException("Google Service Account JSON 파일 경로가 설정되지 않았습니다.");
            }

            log.info("지정된 경로에서 Google Service Account JSON 로드: {}", serviceAccountPath);
            try (InputStream serviceAccountStream = new FileInputStream(serviceAccountPath)) {
                credentials = GoogleCredentials.fromStream(serviceAccountStream)
                        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            }
        }

        log.info("Google Sheets 서비스 객체 생성 완료");

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(googleSheetsProperties.getApplicationName())
                .build();
    }


    public List<List<Object>> getSheetData(String sheetName, String range) throws IOException, GeneralSecurityException {
        log.info("Google Sheets 데이터 조회 시작: Sheet={}, Range={}", sheetName, range);

        Sheets service = getSheetsService();
        String fullRange = sheetName + "!" + range;
        ValueRange response = service.spreadsheets().values()
                .get(googleSheetsProperties.getSpreadsheetId(), fullRange)
                .execute();

        List<List<Object>> values = response.getValues();
        log.info("Google Sheets 데이터 조회 완료 ({}개 행)", values != null ? values.size() : 0);

        return values;
    }
}
