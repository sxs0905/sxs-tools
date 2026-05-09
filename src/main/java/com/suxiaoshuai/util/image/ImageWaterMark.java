package com.suxiaoshuai.util.image;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
     * 水印图片文件（优先于imageIcon）
     */
    private File imageFile;

    /**
     * 水印图片字节数组（优先于imageFile和imageIcon）
     */
    private byte[] imageBytes;

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
     * 获取水印图片文件
     *
     * @return 水印图片文件
     */
    public File getImageFile() {
        return imageFile;
    }

    /**
     * 设置水印图片文件（优先于imageIcon）
     *
     * @param imageFile 水印图片文件
     * @return 当前对象，支持链式调用
     */
    public ImageWaterMark setImageFile(File imageFile) {
        this.imageFile = imageFile;
        return this;
    }

    /**
     * 获取水印图片字节数组
     *
     * @return 水印图片字节数组
     */
    public byte[] getImageBytes() {
        return imageBytes;
    }

    /**
     * 设置水印图片字节数组（优先于imageFile和imageIcon）
     *
     * @param imageBytes 水印图片字节数组
     * @return 当前对象，支持链式调用
     */
    public ImageWaterMark setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
        return this;
    }

    /**
     * 从输入流读取并设置水印图片字节数组
     *
     * @param inputStream 水印图片输入流
     * @return 当前对象，支持链式调用
     * @throws IOException 读取输入流失败时抛出
     */
    public ImageWaterMark setImageInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            this.imageBytes = null;
            return this;
        }
        this.imageBytes = inputStream.readAllBytes();
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
