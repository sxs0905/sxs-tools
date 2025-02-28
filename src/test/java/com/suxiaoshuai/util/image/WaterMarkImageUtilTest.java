package com.suxiaoshuai.util.image;

import org.junit.jupiter.api.Test;

import javax.swing.*;

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
    void localImage() {
        String origin = "/Desktop/20240326173745880347.png";
        String dest = "/Desktop/2.png";
        String icon = "/image/Family/H.ico";
        ImageWaterMark waterMark = new ImageWaterMark();
        waterMark.setImageIcon(new ImageIcon(icon));
        WaterMarkImageUtil.localImage(origin, dest, waterMark);
    }
}