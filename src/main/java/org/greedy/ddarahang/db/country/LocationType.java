package org.greedy.ddarahang.db.country;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.greedy.ddarahang.common.exception.NotFoundCountryException;

@Getter
@AllArgsConstructor
public enum LocationType {
    DOMESTIC,
    INTERNATIONAL;

    public static LocationType getLocationType(String Country) {
        if(Country == null || Country.isEmpty()) {
            throw new NotFoundCountryException("Country is null or empty");
        }

        if(Country.equals("대한민국")) {
            return DOMESTIC;
        }

        return INTERNATIONAL;
    }
}
