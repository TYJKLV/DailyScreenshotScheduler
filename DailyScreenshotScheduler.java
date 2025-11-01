package com.yyk;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class DailyScreenshotScheduler {

    public static void main(String[] args) {
        scheduleDailyScreenshot(10, 07);
    }

    public static void scheduleDailyScreenshot(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前时间: " + now);

        LocalDateTime targetTime = now.withHour(hour)
                                    .withMinute(minute)
                                    .withSecond(0)
                                    .withNano(0);

        if (now.compareTo(targetTime) > 0) {
            targetTime = targetTime.plusDays(1);
        }

        System.out.println("下次执行时间: " + targetTime);

        long initialDelay = Duration.between(now, targetTime).toMillis();
        long period = 1000 * 60 * 60 * 24;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("执行每日截图...");
            takeScreenshotWithRealResolution();
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取真实物理分辨率的方法
     */
    public static Dimension getRealScreenSize() {
        try {
            // 方法1: 使用GraphicsEnvironment获取最大边界 (逻辑分辨率)
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle maximumBounds = ge.getMaximumWindowBounds();
            System.out.println("最大窗口边界: " + maximumBounds);

            // 方法2: 尝试通过反射获取真实分辨率
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            DisplayMode dm = gd.getDisplayMode();
            System.out.println("显示模式: " + dm.getWidth() + "x" + dm.getHeight());

            // 返回较大的尺寸（应该是物理分辨率）
            int width = Math.max(maximumBounds.width, dm.getWidth());
            int height = Math.max(maximumBounds.height, dm.getHeight());

            System.out.println("计算出的物理分辨率: " + width + "x" + height);
            return new Dimension(width, height);

        } catch (Exception e) {
            System.err.println("获取真实分辨率失败，使用默认方法");
            return Toolkit.getDefaultToolkit().getScreenSize();
        }
    }

    /**
     * 使用真实物理分辨率截图
     */
    public static void takeScreenshotWithRealResolution() {
        try {
            Robot robot = new Robot();

            // 获取真实物理分辨率
            Dimension realSize = getRealScreenSize();
            Rectangle screenRect = new Rectangle(realSize);

            System.out.println("截取区域: " + screenRect);

            // 截取整个物理屏幕
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            System.out.println("实际截图尺寸: " + screenshot.getWidth() + "x" + screenshot.getHeight());

            // 保存图片
            saveScreenshot(screenshot);

        } catch (Exception e) {
            System.err.println("❌ 截图失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 保存截图到文件
     */
    private static void saveScreenshot(BufferedImage screenshot) {
        try {
            String timestamp = LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            );
            String filename = "D:/daily_screenshots/daily_" + timestamp + ".png";

            File dir = new File("D:/daily_screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ImageIO.write(screenshot, "png", new File(filename));
            System.out.println("✅ 截图已保存: " + filename);

        } catch (Exception e) {
            System.err.println("❌ 保存图片失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}