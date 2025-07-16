package org.greedy.ddarahang.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.db.travelCourse.TravelCourseRepository;
import org.greedy.ddarahang.db.video.VideoRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final TravelCourseRepository travelCourseRepository;

    public void updateViewCount(Long videoId, int newCount) {
        travelCourseRepository.updateVideoViewCountByVideoId(videoId, String.valueOf(newCount));
        log.info("viewCount 업데이트 완료 - videoId: {}, count: {}", videoId, newCount);
    }

}
