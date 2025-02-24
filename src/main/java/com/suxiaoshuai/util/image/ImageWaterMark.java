package com.suxiaoshuai.util.image;

import javax.swing.*;

public final class ImageWaterMark extends WaterMarkInfo {
    /**
     * 水印图片
     * 一般为gif或者png的，这样可设置透明度
     */
    private ImageIcon imageIcon;

    /**
     * 水印宽高模式
     */
    private ScaleMode scaleMode = ScaleMode.ORIGINAL;
    /**
     * 水印图片宽度
     */
    private int watermarkWidth = -1;
    /**
     * 水印图片高度
     */
    private int watermarkHeight = -1;


    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public ImageWaterMark setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        return this;
    }

    public int getWatermarkWidth() {
        return watermarkWidth;
    }

    public void setWatermarkWidth(int watermarkWidth) {
        if (watermarkWidth < 0) throw new IllegalArgumentException("宽度必须≥0");
        this.watermarkWidth = watermarkWidth;
    }

    public int getWatermarkHeight() {
        return watermarkHeight;
    }

    public void setWatermarkHeight(int watermarkHeight) {
        if (watermarkHeight < 0) throw new IllegalArgumentException("高度必须≥0");
        this.watermarkHeight = watermarkHeight;
    }

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public ImageWaterMark setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        return this;
    }
}
