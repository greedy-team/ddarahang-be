package org.greedy.ddarahang.common.heatlth;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isHealthy = checkSomeCondition();
        if (isHealthy) {
            return Health.up().withDetail("customHealth", "All systems go!").build();
        } else {
            return Health.down().withDetail("customHealth", "Something is wrong!").build();
        }
    }

    private boolean checkSomeCondition() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        return freeMemory > (totalMemory * 0.15);
    }
}