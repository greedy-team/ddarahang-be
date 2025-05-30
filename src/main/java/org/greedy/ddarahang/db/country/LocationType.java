package org.greedy.ddarahang.db.country;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationType {
    DOMESTIC,
    INTERNATIONAL;

    public static LocationType getLocationType(String Country) {

        if(Country.equals("대한민국")) {
            return DOMESTIC;
        }

        return INTERNATIONAL;
    }
}
