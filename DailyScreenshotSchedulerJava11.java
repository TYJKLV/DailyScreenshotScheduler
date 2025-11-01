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
    public static LocalDateTime targetTime;

    static {
        // Java 11+ 必需的DPI感知设置
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    public static void main(String[] args) {
        scheduleDailyScreenshot(11, 44);
    }

    public static void scheduleDailyScreenshot(int hour, int minute) {
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
    public static void takeScreenshotForJava11() {
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

            // 保存图片
            saveScreenshot(screenshot);
            System.out.println("下次执行时间：" + targetTime.plusDays(1));

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