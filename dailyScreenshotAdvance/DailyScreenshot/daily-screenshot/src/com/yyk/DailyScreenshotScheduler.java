package com.yyk;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


class DailyScreenshotScheduler {
    // 任务调度线程池  核心线程数：1
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // 下次执行随机时间
    private static LocalDateTime nextRandomTime;
    // 测试变量
    private static boolean test = true;
    // 截图保存目录
    private static final String DIRECTORY_PATH = "D:/daily_screenshots";

    // 静态初始化块：只注册一次关闭钩子，优雅关闭线程池
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                System.out.println("截图调度器已关闭");
            }
        }));
    }

    /**
     * 每日固定时间截图
     *
     * @param isWatermark true:带水印  false:不带水印
     * @param hour
     * @param minute
     */
    public static void startScreenshotSchedulerWithWatermark(boolean isWatermark, int hour, int minute) {
        if (isWatermark) {
            scheduleDailyScreenshotWithWatermark(hour, minute);
        } else {
            scheduleDailyScreenshot(hour, minute);
        }
    }

    /**
     * 每日随机时间截图
     *
     * @param isWatermark true:带水印  false:不带水印
     */
    public static void startRandomScreenshotSchedulerWithWatermark(boolean isWatermark) {
        if (isWatermark) {
            scheduleNextRandomScreenshotWithWatermark();
        } else {
            scheduleNextRandomScreenshot();
        }
    }

    /**
     * 每日固定时间截图
     *
     * @param hour
     * @param minute
     */
    private static void scheduleDailyScreenshot(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前时间: " + now);

        //目标时间 ，即任务执行时间
        LocalDateTime targetTime = now.withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);

        // 若 targetTime已在今天过去，则加一天，明天执行
        if (now.compareTo(targetTime) > 0) {
            targetTime = targetTime.plusDays(1);
        }

        System.out.println("下次截图时间: " + targetTime);

        // 延迟时间：延长一定时间才开始执行任务
        long initialDelay = Duration.between(now, targetTime).toMillis();
        long period = 1000 * 60 * 60 * 24;
        LocalDateTime finalTargetTime = targetTime;
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("执行每日截图...");
            BufferedImage screenshot = ScreenShot.takeScreenshotWithRealResolution();
            SaveScreenshot.save(DIRECTORY_PATH, screenshot);
            System.out.println("========================");
            // 计算实际的下次执行时间
            LocalDateTime nextTime = LocalDateTime.now().plusDays(1)
                    .withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            System.out.println("下次执行时间 " + nextTime);
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 每日固定时间截图 带水印
     *
     * @param hour
     * @param minute
     */
    private static void scheduleDailyScreenshotWithWatermark(int hour, int minute) {
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
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("执行每日截图...");
            BufferedImage screenshot = ScreenShot.takeScreenshotWithWatermark();
            SaveScreenshot.save(DIRECTORY_PATH, screenshot);
            System.out.println("========================");

            // 计算实际的下次执行时间
            LocalDateTime nextTime = LocalDateTime.now().plusDays(1)
                    .withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            System.out.println("下次执行时间 " + nextTime);
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }


    /**
     * 安排下一次随机时间截图
     */
    private static void scheduleNextRandomScreenshot() {
        LocalDateTime now = LocalDateTime.now();
        nextRandomTime = RandomTime.generateRandomTime();

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
            BufferedImage screenshot = ScreenShot.takeScreenshotWithRealResolution();
            SaveScreenshot.save(DIRECTORY_PATH, screenshot);
            // 执行完成后，安排下一次随机截图
            scheduleNextRandomScreenshot(); // 递归调度

        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 安排下一次带水印的随机时间截图
     */
    private static void scheduleNextRandomScreenshotWithWatermark() {
        LocalDateTime now = LocalDateTime.now();
        nextRandomTime = RandomTime.generateRandomTime();

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
            BufferedImage screenshot = ScreenShot.takeScreenshotWithWatermark();// 使用带水印的截图方法
            SaveScreenshot.save(DIRECTORY_PATH, screenshot);
            // 执行完成后，安排下一次随机截图
            scheduleNextRandomScreenshotWithWatermark();  // 递归调度

        }, delay, TimeUnit.MILLISECONDS);
    }
}