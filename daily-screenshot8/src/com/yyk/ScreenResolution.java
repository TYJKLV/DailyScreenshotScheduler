package com.yyk;

import java.awt.*;

/**
 * @author tk
 * 获取屏幕分辨率(缩放、显示器分辨率共同影响)
 */
class ScreenResolution {
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

}
