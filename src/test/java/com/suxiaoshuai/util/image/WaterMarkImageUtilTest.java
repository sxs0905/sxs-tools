package com.suxiaoshuai.util.image;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
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
    void image() throws Exception {
        String origin = "/Users/han/Desktop/2.png";
        // String icon = "/Users/han/Desktop/favicon.png";
        String icon = "/Users/han/Desktop/favicon.ico";
        File iconFile = new File(icon);
        assertTrue(iconFile.exists(), "水印图片不存在: " + iconFile.getAbsolutePath());

        ImageWaterMark waterMark = new ImageWaterMark();
        waterMark.setImageInputStream(new FileInputStream(iconFile));

        TextWaterMark textWaterMark = new TextWaterMark();
        textWaterMark.setText("sxs");
        textWaterMark.setFont(new Font("微软雅黑", Font.BOLD, 30));
        textWaterMark.setColor(Color.decode("#FF0000"));
        textWaterMark.setStrokeEnabled(true);
        textWaterMark.setStrokeColor(Color.WHITE);
        textWaterMark.setStrokeWidth(2f);

        WaterMarkLayoutOptions options = new WaterMarkLayoutOptions()
            .setDirection(WaterMarkLayoutOptions.Direction.HORIZONTAL)
            .setOrder(WaterMarkLayoutOptions.Order.TEXT_FIRST)
            .setAlign(WaterMarkLayoutOptions.Align.CENTER)
            .setElementSpacing(10)
            .setPosition(WaterMarkLayoutOptions.Position.BOTTOM_RIGHT)
            .setMarginX(20)
            .setMarginY(20)
            .setTiled(false);

        WaterMarkImageUtil.watermark(new File(origin), null, waterMark, textWaterMark, options);
    }

    @Test
    void imageByBytes() throws Exception {
        File originFile = new File("/Users/han/Desktop/2.png");
        File iconFile = new File("/Users/han/Desktop/favicon.png");
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

        byte[] resultBytes = WaterMarkImageUtil.watermark(originBytes, iconBytes, textWaterMark, options);
        Files.write(new File("/Users/han/Desktop/2_wm_bytes.png").toPath(), resultBytes);
    }
}