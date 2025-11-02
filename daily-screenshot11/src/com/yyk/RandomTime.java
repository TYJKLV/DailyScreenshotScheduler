package com.yyk;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author tk
 * 生成随机时间 (9:00 - 23:59)
 */
class RandomTime {
    private static final Random random = new Random();

    /**
     * 生成随机时间 (9:00 - 23:59)
     * 如果今天还有时间就在今天，否则在明天
     */

    public static LocalDateTime generateRandomTime() {
        LocalDateTime now = LocalDateTime.now();

        // 生成随机小时 (9-23) 和随机分钟 (0-59)
        int randomHour = random.nextInt(15) + 9;    // 9到23
        int randomMinute = random.nextInt(60);      // 0到59

        LocalDateTime randomTimeToday = now.withHour(randomHour)
                .withMinute(randomMinute)
                .withSecond(0)
                .withNano(0);

        // 如果今天的随机时间已经过了，就安排到明天
        if (now.compareTo(randomTimeToday) > 0) {
            return randomTimeToday.plusDays(1);
        }

        return randomTimeToday;
    }

}
