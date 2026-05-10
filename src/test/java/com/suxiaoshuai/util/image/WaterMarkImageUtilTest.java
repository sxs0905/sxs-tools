package com.suxiaoshuai.util.image;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WaterMarkImageUtilTest {

    @Test
    void markImageByIcon() {
    }

    @Test
    void markImageByText() {
    }

    @Test
    void localText() {
    }

    @Test
    void imageByBytes() throws Exception {
        File originFile = new File("/Users/han/Desktop/1.jpg");
        // File iconFile = new File("/Users/han/Desktop/favicon.ico");
        File iconFile = new File("/Users/han/Desktop/2.png");
        assertTrue(originFile.exists(), "源图片不存在: " + originFile.getAbsolutePath());
        assertTrue(iconFile.exists(), "水印图片不存在: " + iconFile.getAbsolutePath());

        byte[] originBytes = Files.readAllBytes(originFile.toPath());
        byte[] iconBytes = Files.readAllBytes(iconFile.toPath());

        TextWaterMark textWaterMark = new TextWaterMark();
        textWaterMark.setText("sxs");
        textWaterMark.setFont(new Font("微软雅黑", Font.BOLD, 30));
        textWaterMark.setColor(Color.decode("#FF0000"));

        WaterMarkLayoutOptions options = new WaterMarkLayoutOptions()
                .setDirection(WaterMarkLayoutOptions.Direction.HORIZONTAL)
                .setOrder(WaterMarkLayoutOptions.Order.TEXT_FIRST)
                .setAlign(WaterMarkLayoutOptions.Align.CENTER)
                .setElementSpacing(10)
                .setPosition(WaterMarkLayoutOptions.Position.BOTTOM_RIGHT)
                .setMarginX(20)
                .setMarginY(20)
                .setTiled(false);

        byte[] resultBytes = WaterMarkImageUtil.watermark(originBytes, iconBytes, null, options);
        Files.write(new File("/Users/han/Desktop/2_wm_bytes.png").toPath(), resultBytes);
    }

    @Test
    void text() {
        File originFile = new File("/Users/han/Desktop/1.jpg");
        TextWaterMark textWaterMark = new TextWaterMark();
        textWaterMark.setText("sxs");
        textWaterMark.setFont(new Font("微软雅黑", Font.BOLD, 30));
        textWaterMark.setColor(Color.decode("#FF0000"));
        WaterMarkImageUtil.text(originFile, new File("/Users/han/Desktop/2_wm_text.png"), textWaterMark);
    }

    @Test
    void image() {
        File originFile = new File("/Users/han/Desktop/1.jpg");
        ImageIcon imageIcon = new ImageIcon("/Users/han/Desktop/favicon.ico");
        ImageWaterMark imageWaterMark = new ImageWaterMark();
        imageWaterMark.setImageIcon(imageIcon);
        imageWaterMark.setWatermarkHeight(100);
        WaterMarkImageUtil.image(originFile, new File("/Users/han/Desktop/2_wm_image.png"), imageWaterMark);
    }
}
