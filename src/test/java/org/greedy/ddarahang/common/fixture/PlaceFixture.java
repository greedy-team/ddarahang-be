package org.greedy.ddarahang.common.fixture;

import org.greedy.ddarahang.db.place.Place;
import org.greedy.ddarahang.db.region.Region;

public class PlaceFixture {
    public static Place getMockPlace_1(Region region) {
        return Place.builder()
                .name("경복궁")
                .address("서울 종로구 세종로 1-1")
                .region(region)
                .build();
    }

    public static Place getMockPlace_2(Region region) {
        return Place.builder()
                .name("광안리 해수욕장")
                .address("부산 광안리")
                .region(region)
                .build();
    }
}
