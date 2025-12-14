package com.yyk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Watermark helper that accepts a custom text
 * and draws it with a background box in one of
 * the four corners of the image, chosen at random.
 *
 * The font size is adjusted based on the image size
 * and the length of the watermark text.
 *
 * The watermark text automatically appends a timestamp
 * in the format yyyy:MM:dd hh:mm:ss.
 */
public class RandomCornerWatermark {

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    private RandomCornerWatermark() {
        // utility class
    }

    public static void addWatermark(BufferedImage image, String text) {
        String baseText = (text == null) ? "" : text.trim();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        String finalText = baseText.isEmpty()
                ? timestamp
                : baseText + " | " + timestamp;

        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Base font and dynamic scaling (start smaller).
            int baseSize = Math.max(14, image.getWidth() / 80);
            Font font = new Font("SansSerif", Font.BOLD, baseSize);
            g2d.setFont(font);

            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(finalText);
            int textHeight = metrics.getHeight();

            // Limit the watermark width to at most 60% of the image width.
            int maxTextWidth = (int) (image.getWidth() * 0.6);
            if (textWidth > maxTextWidth) {
                double ratio = (double) maxTextWidth / (double) textWidth;
                int newSize = (int) Math.max(14, baseSize * ratio);
                font = font.deriveFont((float) newSize);
                g2d.setFont(font);
                metrics = g2d.getFontMetrics();
                textWidth = metrics.stringWidth(finalText);
                textHeight = metrics.getHeight();
            }

            int padding = 8;
            int rectPadding = 8;
            int cornerRadius = 10;

            int rectWidth = textWidth + rectPadding * 2;
            int rectHeight = textHeight + rectPadding;

            // Randomly choose one of the four corners:
            // 0: top-left, 1: top-right, 2: bottom-left, 3: bottom-right.
            int corner = RANDOM.nextInt(4);

            int rectX;
            int rectY;

            switch (corner) {
                case 0: // top-left
                    rectX = padding;
                    rectY = padding;
                    break;
                case 1: // top-right
                    rectX = image.getWidth() - padding - rectWidth;
                    rectY = padding;
                    break;
                case 2: // bottom-left
                    rectX = padding;
                    rectY = image.getHeight() - padding - rectHeight;
                    break;
                case 3: // bottom-right
                default:
                    rectX = image.getWidth() - padding - rectWidth;
                    rectY = image.getHeight() - padding - rectHeight;
                    break;
            }

            int textX = rectX + rectPadding;
            int textY = rectY + rectPadding + metrics.getAscent();

            // Background box: solid black for high contrast.
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, cornerRadius, cornerRadius);

            // Optional border: solid white, thin.
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(rectX, rectY, rectWidth, rectHeight, cornerRadius, cornerRadius);

            // Draw text.
            g2d.setColor(Color.WHITE);
            g2d.drawString(finalText, textX, textY);

            System.out.println("[Watermark] Image: " + image.getWidth() + "x" + image.getHeight());
            System.out.println("[Watermark] Corner: " + corner);
            System.out.println("[Watermark] Rect position: (" + rectX + ", " + rectY + ") "
                    + rectWidth + "x" + rectHeight);
            System.out.println("[Watermark] Text: \"" + finalText + "\"");
        } finally {
            g2d.dispose();
        }
    }
}
