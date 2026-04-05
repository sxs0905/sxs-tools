package com.suxiaoshuai.util.image;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
    void image() {
        String origin = "/Users/han/Desktop/2.png";
        String icon = "/Users/han/Desktop/favicon.ico";
        // String icon = "/Users/han/Desktop/favicon.png";
        File iconFile = new File(icon);
        assertTrue(iconFile.exists(), "水印图片不存在: " + iconFile.getAbsolutePath());

        ImageWaterMark waterMark = new ImageWaterMark();
        waterMark.setImageFile(iconFile);

        TextWaterMark textWaterMark = new TextWaterMark();
        textWaterMark.setText("sxs");
        textWaterMark.setFont(new Font("微软雅黑", Font.BOLD, 30));
        textWaterMark.setColor(Color.decode("#FF0000"));

        WaterMarkImageUtil.watermark(new File(origin), null, waterMark, textWaterMark);
    }
}