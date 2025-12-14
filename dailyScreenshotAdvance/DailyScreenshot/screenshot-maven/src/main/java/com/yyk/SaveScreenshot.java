package com.yyk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;

/**
 * @author tk
 * 保存文件到硬盘中
 */
class SaveScreenshot {
    public static void save(String directoryPath, BufferedImage screenshot) {
        try {
            String timestamp = LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            );
            String fileName = "daily_" + timestamp + ".png";
            String fullPath = directoryPath + "/" + fileName;

            File dir = new File(directoryPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ImageIO.write(screenshot, "png", new File(fullPath));
            System.out.println("✅ 截图已保存: " + fullPath);

        } catch (Exception e) {
            System.err.println("❌ 保存图片失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
