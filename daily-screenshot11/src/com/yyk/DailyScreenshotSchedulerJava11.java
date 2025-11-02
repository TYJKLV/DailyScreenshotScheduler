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

// java11
public class DailyScreenshotSchedulerJava11 {
    // 任务调度线程池  核心线程数：1
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 下次执行随机时间
    private static LocalDateTime nextRandomTime;

    // 测试变量
    private static boolean test = true;
    private static LocalDateTime targetTime;
    private static final String DIRECTORY_PATH = "D:/daily_screenshots";

    static {
        // Java 11+ 必需的DPI感知设置
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                System.out.println("截图调度器已关闭");
            }
        }));
    }

    // public static void main(String[] args) {
    //     scheduleNextRandomScreenshotWithWatermark();
    // }

    public static void scheduleNextRandomScreenshotWithWatermark() {
        LocalDateTime now = LocalDateTime.now();
        nextRandomTime = RandomTime.generateRandomTime();

        long delay = Duration.between(now, nextRandomTime).toMillis();
        if (test) {
            delay = 10000;
            test = false;
        }
        System.out.println("=== 带水印截图调度信息 ===");
        System.out.println("当前时间: " + now);
        System.out.println("下次截图时间: " + nextRandomTime);
        System.out.println("========================");

        scheduler.schedule(() -> {
            System.out.println("🎯 执行带水印随机时间截图...");
            takeScreenshotForJava11();
            // 执行完成后，安排下一次随机截图
            scheduleNextRandomScreenshotWithWatermark();  // 递归调度
        }, delay, TimeUnit.MILLISECONDS);
    }

    private static void scheduleDailyScreenshot(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前时间: " + now);

        targetTime = now.withHour(hour)
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
            takeScreenshotForJava11();
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Java 11专用截图方法
     */
    private static void takeScreenshotForJava11() {
        try {
            Robot robot = new Robot();

            // 对于Java 11，直接使用物理分辨率，但需要考虑缩放
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            DisplayMode dm = gd.getDisplayMode();

            // 获取物理分辨率
            int physicalWidth = dm.getWidth();  // 2560
            int physicalHeight = dm.getHeight(); // 1600

            System.out.println("物理分辨率: " + physicalWidth + "x" + physicalHeight);

            // 创建截取区域
            Rectangle screenRect = new Rectangle(physicalWidth, physicalHeight);
            System.out.println("截取区域: " + screenRect);

            // 截取整个物理屏幕
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            System.out.println("实际截图尺寸: " + screenshot.getWidth() + "x" + screenshot.getHeight());

            Watermark.addWatermarkToImage(screenshot);
            // 保存图片
            SaveScreenshot.save(DIRECTORY_PATH, screenshot);
        } catch (Exception e) {
            System.err.println("❌ 截图失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}