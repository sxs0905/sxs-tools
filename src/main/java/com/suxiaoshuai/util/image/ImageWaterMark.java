package com.suxiaoshuai.util.image;

import javax.swing.*;

/**
 * 图片水印信息类
 */
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


    /**
     * 获取水印图片
     *
     * @return 水印图片对象
     */
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    /**
     * 设置水印图片
     *
     * @param imageIcon 水印图片对象
     * @return 当前对象，支持链式调用
     */
    public ImageWaterMark setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        return this;
    }

    /**
     * 获取水印图片宽度
     *
     * @return 水印图片宽度，-1表示使用原始宽度
     */
    public int getWatermarkWidth() {
        return watermarkWidth;
    }

    /**
     * 设置水印图片宽度
     *
     * @param watermarkWidth 水印图片宽度（像素）
     * @throws IllegalArgumentException 当宽度小于0时抛出异常
     */
    public void setWatermarkWidth(int watermarkWidth) {
        if (watermarkWidth < 0) throw new IllegalArgumentException("宽度必须≥0");
        this.watermarkWidth = watermarkWidth;
    }

    /**
     * 获取水印图片高度
     *
     * @return 水印图片高度，-1表示使用原始高度
     */
    public int getWatermarkHeight() {
        return watermarkHeight;
    }

    /**
     * 设置水印图片高度
     *
     * @param watermarkHeight 水印图片高度（像素）
     * @throws IllegalArgumentException 当高度小于0时抛出异常
     */
    public void setWatermarkHeight(int watermarkHeight) {
        if (watermarkHeight < 0) throw new IllegalArgumentException("高度必须≥0");
        this.watermarkHeight = watermarkHeight;
    }

    /**
     * 获取水印图片缩放模式
     *
     * @return 缩放模式
     */
    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    /**
     * 设置水印图片缩放模式
     *
     * @param scaleMode 缩放模式
     * @return 当前对象，支持链式调用
     */
    public ImageWaterMark setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        return this;
    }
}
