package org.greedy.ddarahang.db.locationType;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationType {
    DOMESTIC("국내"),
    INTERNATIONAL("해외");

    private String description;
}