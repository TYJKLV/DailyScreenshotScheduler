package com.yyk;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author tk
 * 为截图添加水印
 */
class Watermark {
    public static void addWatermarkToImage(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 生成水印文本
        String watermarkText = WatermarkText.generateWatermarkText();

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
}
