package com.yyk;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author tk
 * 生成水印文本
 */
class WatermarkText {

    // 获取windows用户名
    private static final String USER_NAME = System.getProperty("user.name");

    public static String generateWatermarkText() {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        return USER_NAME + " | " + timestamp;
    }
}
