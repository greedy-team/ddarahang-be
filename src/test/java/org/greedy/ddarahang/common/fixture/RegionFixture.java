package org.greedy.ddarahang.common.fixture;

import org.greedy.ddarahang.db.country.Country;
import org.greedy.ddarahang.db.region.Region;

public class RegionFixture {
    public static Region getMockRegion_1(Country country) {
        return Region.builder()
                .name("서울")
                .country(country)
                .build();
    }

    public static Region getMockRegion_2(Country country) {
        return Region.builder()
                .name("부산")
                .country(country)
                .build();
    }
}
