package com.yyk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

// 每天随机时间截图，带有水印
public class DailyScreenshotSchedulerWithWatermark {

    private static final Random random = new Random();
    private static ScheduledExecutorService scheduler;
    private static LocalDateTime nextRandomTime;
    private static boolean test = true;
    // 常量定义
    private static final int TEXT_AREA_HEIGHT = 50; // 文字区域高度

    // === 新增的水印配置 ===
    private static final String USER_NAME = System.getProperty("user.name");
    private static final Font WATERMARK_FONT = new Font("微软雅黑", Font.BOLD, 36);
    private static final Color WATERMARK_COLOR = new Color(255, 255, 255, 200);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 150);
    // === 水印配置结束 ===

    public static void main(String[] args) {
        // 使用带水印的版本启动
        // startRandomScreenshotSchedulerWithWatermark();
        startRandomScreenshotScheduler();
    }


    // === 新增方法：带水印的调度器启动 ===

    /**
     * 启动带水印的随机时间截图调度器
     */
    public static void startRandomScreenshotSchedulerWithWatermark() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduleNextRandomScreenshotWithWatermark();
        // 添加关闭钩子，优雅关闭线程池
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                System.out.println("截图调度器已关闭");
            }
        }));
    }
    // === 新增方法：带水印的调度 ===

    /**
     * 安排下一次带水印的随机时间截图
     */
    private static void scheduleNextRandomScreenshotWithWatermark() {
        LocalDateTime now = LocalDateTime.now();
        nextRandomTime = generateRandomTime();

        long delay = Duration.between(now, nextRandomTime).toMillis();
        if (test) {
            delay = 3000;
            test = false;
        }
        System.out.println("=== 带水印截图调度信息 ===");
        System.out.println("当前时间: " + now);
        System.out.println("下次截图时间: " + nextRandomTime);
        System.out.println("========================");

        scheduler.schedule(() -> {
            System.out.println("🎯 执行带水印随机时间截图...");
            takeScreenshotWithWatermark();  // 使用带水印的截图方法

            // 执行完成后，安排下一次随机截图
            scheduleNextRandomScreenshotWithWatermark();

        }, delay, TimeUnit.MILLISECONDS);
    }

    // === 新增方法：添加带背景框的水印 ===

    /**
     * 在图片上添加带背景框的水印
     */
    // === 新增方法：添加明显背景框的水印 ===

    /**
     * 在图片上添加明显背景框的水印
     */
    private static void addWatermarkToImage2(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 生成水印文本
        String watermarkText = generateWatermarkText();

        // 设置字体
        Font font = new Font("微软雅黑", Font.BOLD, 32);
        g2d.setFont(font);

        // 计算文字尺寸
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(watermarkText);
        int textHeight = metrics.getHeight();

        // 设置水印位置和样式
        int padding = 25; // 边距
        int rectPadding = 15; // 背景框内边距
        int cornerRadius = 12; // 圆角半径

        // 修复：正确的背景框高度计算
        int rectX = padding;
        int rectY = image.getHeight() - padding - textHeight - rectPadding * 2; // 修复：正确计算Y坐标
        int rectWidth = textWidth + rectPadding * 2;
        int rectHeight = textHeight + rectPadding; // 修复：保持合理高度

        // 修复：正确的文字位置计算
        int textX = rectX + rectPadding;
        int textY = rectY + rectPadding + metrics.getAscent(); // 修复：使用getAscent()确保文字居中

        System.out.println("水印信息:");
        System.out.println("  - 图片尺寸: " + image.getWidth() + "x" + image.getHeight());
        System.out.println("  - 文字尺寸: " + textWidth + "x" + textHeight);
        System.out.println("  - 背景框位置: (" + rectX + ", " + rectY + ") " + rectWidth + "x" + rectHeight);
        System.out.println("  - 文字位置: (" + textX + ", " + textY + ")");
        System.out.println("  - 水印内容: " + watermarkText);

        // 1. 绘制深色半透明背景框
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, cornerRadius, cornerRadius);

        // 2. 可选：绘制边框
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(rectX, rectY, rectWidth, rectHeight, cornerRadius, cornerRadius);

        // 3. 绘制白色文字
        g2d.setColor(Color.WHITE);
        g2d.drawString(watermarkText, textX, textY);

        g2d.dispose();

        System.out.println("✅ 水印添加成功，图片尺寸保持不变: " + image.getWidth() + "x" + image.getHeight());
    }
    // === 新增方法：带水印的截图 ===

    /**
     * 使用真实物理分辨率截图并添加水印
     */
    public static void takeScreenshotWithWatermark() {
        try {
            Robot robot = new Robot();

            // 使用原有的获取分辨率方法
            Dimension realSize = getRealScreenSize();
            Rectangle screenRect = new Rectangle(realSize);

            System.out.println("截取区域: " + screenRect);

            // 截取整个物理屏幕
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            System.out.println("实际截图尺寸: " + screenshot.getWidth() + "x" + screenshot.getHeight());

            // 添加水印
            addWatermarkToImage2(screenshot);

            // 使用原有的保存方法
            saveScreenshot(screenshot);

        } catch (Exception e) {
            System.err.println("❌ 截图失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // === 新增方法：添加水印 ===

    /**
     * 在图片上添加水印
     */
    private static void addWatermarkToImage(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 生成水印文本
        String watermarkText = generateWatermarkText();

        // 设置水印位置（左下角）
        int padding = 30;
        int x = padding;
        int y = image.getHeight() - padding;

        System.out.println("水印信息:");
        System.out.println("  - 图片尺寸: " + image.getWidth() + "x" + image.getHeight());
        System.out.println("  - 水印位置: (" + x + ", " + y + ")");
        System.out.println("  - 水印内容: " + watermarkText);

        // 绘制文字阴影
        g2d.setFont(WATERMARK_FONT);
        g2d.setColor(SHADOW_COLOR);
        g2d.drawString(watermarkText, x + 2, y + 2);

        // 绘制主要文字
        g2d.setColor(WATERMARK_COLOR);
        g2d.drawString(watermarkText, x, y);

        g2d.dispose();

        System.out.println("✅ 水印添加成功");
    }

    // === 新增方法：生成水印文本 ===

    /**
     * 生成水印文本
     */
    private static String generateWatermarkText() {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        return USER_NAME + " | " + timestamp;
    }


    // ========== 以下是原有代码，完全保持不变 ==========

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
            delay = 3000;
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