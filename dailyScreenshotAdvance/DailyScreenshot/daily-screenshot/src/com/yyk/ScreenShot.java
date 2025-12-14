package com.yyk;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author tk
 * 截图
 */
class ScreenShot {

    // 截图
    public static BufferedImage takeScreenshotWithRealResolution() {
        try {
            Robot robot = new Robot();

            // 获取真实物理分辨率
            Dimension realSize = ScreenResolution.getRealScreenSize();
            Rectangle screenRect = new Rectangle(realSize);

            System.out.println("截取区域: " + screenRect);

            // 截取整个物理屏幕
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            System.out.println("实际截图尺寸: " + screenshot.getWidth() + "x" + screenshot.getHeight());

            // 返回BufferedImage对象
            return screenshot;

        } catch (Exception e) {
            System.err.println("❌ 截图失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 带水印的截图
    public static BufferedImage takeScreenshotWithWatermark() {
        try {
            // 获取 截图对象
            BufferedImage screenshot = ScreenShot.takeScreenshotWithRealResolution();
            // 添加水印
            Watermark.addWatermarkToImage(screenshot);
            return screenshot;

        } catch (Exception e) {
            System.err.println("❌ 截图失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
