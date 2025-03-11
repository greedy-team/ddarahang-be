package org.greedy.ddarahang.api.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.greedy.ddarahang.common.config.GoogleSheetsProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleSheetService {

    private final GoogleSheetsProperties googleSheetsProperties;
    private static final String SPREADSHEET_ID = "1FN2MLyVqHEKBQrHJI4K-fNa9z4CHRu8sVVlfeFmdvUs";

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials;

        try (InputStream serviceAccountStream = new ClassPathResource("ddarahang-684ec417dfa8.json").getInputStream()) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        } catch (IOException e) {
            String serviceAccountPath = googleSheetsProperties.getServiceAccountKeyPath();
            if (serviceAccountPath == null || serviceAccountPath.isEmpty()) {
                throw new IOException("Google Service Account JSON 파일 경로가 설정되지 않았습니다.");
            }

            try (InputStream serviceAccountStream = new ClassPathResource(serviceAccountPath).getInputStream()) {
                credentials = GoogleCredentials.fromStream(serviceAccountStream)
                        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            }
        }

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(googleSheetsProperties.getApplicationName())
                .build();
    }

    public List<List<Object>> getSheetData(String sheetName, String range) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        String fullRange = sheetName + "!" + range;
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, fullRange)
                .execute();
        return response.getValues();
    }
}
