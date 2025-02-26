package org.greedy.ddarahang.common.fixture;

import org.greedy.ddarahang.db.video.Video;

import java.time.LocalDate;

public class VideoFixture {
    public static Video getMockVideo_1(LocalDate now) {
        return Video.builder()
                .creator("여행유튜버A")
                .title("서울 여행 브이로그")
                .videoUrl("https://youtube.com/v1")
                .thumbnailUrl("https://img.youtube.com/1.jpg")
                .viewCount(10000L)
                .uploadDate(now)
                .build();
    }

    public static Video getMockVideo_2(LocalDate now) {
        return Video.builder()
                .creator("여행유튜버B")
                .title("부산 여행 브이로그")
                .videoUrl("https://youtube.com/v2")
                .thumbnailUrl("https://img.youtube.com/2.jpg")
                .viewCount(20000L)
                .uploadDate(now)
                .build();
    }
}
