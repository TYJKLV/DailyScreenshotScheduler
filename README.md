#### 1.基本介绍

1. Main:程序入口

2. DailyScreenshotScheduler.java：任务调度线程池，定义四个截图方法

   - 每日固定时间截图

   - 每日固定时间截图（带水印）

   - 每日随机时间截图

   - 每日随机时间截图（带水印）

3. RandomTime.java：随机生成时间（9:00 -- 23:59）

4. SaveScreenshot.java：保存截图

5. ScreenResolution.java：计算屏幕的分辨率

6. ScreenShot.java：截图

7. Watermark.java：添加水印，位于左下角

8. WatermarkText.java：设置水印文本

9. TestRandom.java：测试方法
