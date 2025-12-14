package com.yyk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Daily random screenshot scheduler that guarantees
 * at most one screenshot per calendar day.
 *
 * This class does not modify existing behavior, it
 * is an alternative entry point you can run instead
 * of {@link DailyScreenshotSchedulerJava11}.
 */
public class DailyRandomScreenshotOncePerDay {

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();

    private static final String DIRECTORY_PATH = "D:/daily_screenshots";

    /**
     * Range for random time in a day.
     * For example: 9:00 - 23:59.
     */
    private static final int MIN_HOUR = 9;
    private static final int MAX_HOUR = 23;

    private static final Random RANDOM = new Random();

    /**
     * Test helper: when true, the very first
     * screenshot will be executed after a short,
     * fixed delay so you can quickly verify that
     * everything works.
     */
    private static boolean firstRunTestMode = true;
    private static final long FIRST_RUN_TEST_DELAY_MILLIS = 5000L;

    /**
     * Last day when a screenshot was taken in this JVM.
     * This is kept in memory only; if you restart the
     * application, a new screenshot may be taken today.
     */
    private static LocalDate lastScreenshotDate = null;

    static {
        // Enable proper DPI-aware behavior so that screen
        // coordinates and captured image size match the
        // physical resolution of the display.
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    private DailyRandomScreenshotOncePerDay() {
        // utility class
    }

    /**
     * Simple standalone entry for testing:
     * you can run this main method instead of Main.
     * <p>
     * args[0] (optional) - watermark text.
     */
    public static void main(String[] args) {
        String watermarkText = (args != null && args.length > 0)
                ? args[0]
                : "Daily Random Screenshot";
        scheduleDailyRandomScreenshot(watermarkText);
    }

    /**
     * Start daily random screenshot with a custom watermark text.
     * At most one screenshot will be taken per calendar day.
     */
    public static void scheduleDailyRandomScreenshot(String watermarkText) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate targetDate;

        if (lastScreenshotDate == null) {
            // First run in this JVM: try to schedule for today.
            targetDate = now.toLocalDate();
        } else {
            // We already took one today: schedule for next day.
            targetDate = lastScreenshotDate.plusDays(1);
        }

        LocalDateTime targetDateTime = generateRandomTimeForDate(targetDate);

        // If we somehow computed a time in the past (e.g. starting very late),
        // fall back to tomorrow.
        if (Duration.between(now, targetDateTime).toMillis() <= 0) {
            targetDate = now.toLocalDate().plusDays(1);
            targetDateTime = generateRandomTimeForDate(targetDate);
        }

        long delayMillis = Duration.between(now, targetDateTime).toMillis();

        if (firstRunTestMode) {
            // For the first scheduling in this JVM, force
            // a short delay so that a screenshot is taken
            // a few seconds after start, then fall back to
            // real random times for subsequent days.
            delayMillis = FIRST_RUN_TEST_DELAY_MILLIS;
            firstRunTestMode = false;
        }

        System.out.println("=== Daily once-per-day random screenshot ===");
        System.out.println("Current time: " + now);
        System.out.println("Next screenshot time: " + targetDateTime);
        System.out.println("============================================");

        LocalDate finalTargetDate = targetDate;
        SCHEDULER.schedule(() -> {
            System.out.println("[OncePerDay] Executing screenshot task...");
            takeScreenshotWithCustomWatermark(watermarkText);
            lastScreenshotDate = finalTargetDate;

            // After we successfully take one screenshot for this day,
            // immediately schedule the next day.
            scheduleDailyRandomScreenshot(watermarkText);
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Generate a random time for the given date between MIN_HOUR and MAX_HOUR.
     */
    private static LocalDateTime generateRandomTimeForDate(LocalDate date) {
        int randomHour = RANDOM.nextInt(MAX_HOUR - MIN_HOUR + 1) + MIN_HOUR;
        int randomMinute = RANDOM.nextInt(60);

        return date.atTime(randomHour, randomMinute, 0, 0);
    }

    private static void takeScreenshotWithCustomWatermark(String watermarkText) {
        try {
            Robot robot = new Robot();

            // After enabling DPI awareness in the static block above,
            // this logical size should match the real physical size
            // of the primary display, so we can capture directly
            // without any additional scaling.
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRect = new Rectangle(size);

            System.out.println("[OncePerDay] Screen size: "
                    + size.width + "x" + size.height);
            System.out.println("[OncePerDay] Capture area: " + screenRect);

            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            System.out.println("[OncePerDay] Captured image size: "
                    + screenshot.getWidth() + "x" + screenshot.getHeight());

            RandomCornerWatermark.addWatermark(screenshot, watermarkText);

            SaveScreenshot.save(DIRECTORY_PATH, screenshot);
        } catch (Exception e) {
            System.err.println("[OncePerDay] Screenshot failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
