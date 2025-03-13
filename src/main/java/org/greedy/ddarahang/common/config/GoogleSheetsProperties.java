package org.greedy.ddarahang.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.google.sheets")
public class GoogleSheetsProperties {
    private String applicationName;
    private String serviceAccountKeyPath;
    private String spreadsheetId;
}
