package org.greedy.ddarahang.common.fixture;

import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.country.LocationType;

public class CountryFixture {
    public static Country getMockCountry() {
        return Country.builder()
                .name("대한민국")
                .locationType(LocationType.DOMESTIC)
                .build();
    }
}
