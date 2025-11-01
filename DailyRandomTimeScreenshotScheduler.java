package com.yyk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class DailyScreenshotScheduler {

    private static final Random random = new Random();
    private static ScheduledExecutorService scheduler;
    private static LocalDateTime nextRandomTime;
    private static boolean test = true;

    public static void main(String[] args) {
        startRandomScreenshotScheduler();
    }

    /**
     * 启动随机时间截图调度器
     */
    public static void startRandomScreenshotScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduleNextRandomScreenshot();

        // 添加关闭钩子，优雅关闭线程池
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                System.out.println("截图调度器已关闭");
            }
        }));
    }

    /**
     * 安排下一次随机时间截图
     */
    private static void scheduleNextRandomScreenshot() {

        LocalDateTime now = LocalDateTime.now();
        nextRandomTime = generateRandomTime();

        long delay = Duration.between(now, nextRandomTime).toMillis();
        if (test) {
            delay = 10000;
            test = false;
        }
        System.out.println("=== 截图调度信息 ===");
        System.out.println("当前时间: " + now);
        System.out.println("下次截图时间: " + nextRandomTime);
        System.out.println("==================");

        scheduler.schedule(() -> {
            System.out.println("🎯 执行随机时间截图...");
            takeScreenshotWithRealResolution();

            // 执行完成后，安排下一次随机截图
            scheduleNextRandomScreenshot();

        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 生成随机时间 (9:00 - 23:59)
     * 如果今天还有时间就在今天，否则在明天
     */
    private static LocalDateTime generateRandomTime() {
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

    /**
     * 获取真实物理分辨率的方法
     */
    public static Dimension getRealScreenSize() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle maximumBounds = ge.getMaximumWindowBounds();
            System.out.println("最大窗口边界: " + maximumBounds);

            GraphicsDevice gd = ge.getDefaultScreenDevice();
            DisplayMode dm = gd.getDisplayMode();
            System.out.println("显示模式: " + dm.getWidth() + "x" + dm.getHeight());

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
    public static void saveScreenshot(BufferedImage screenshot) {
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