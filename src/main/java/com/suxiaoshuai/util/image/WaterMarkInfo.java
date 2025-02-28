package com.suxiaoshuai.util.image;

import java.awt.*;

/**
 * 水印信息基础类
 * 包含水印的基本属性，如旋转角度、透明度、偏移量等
 *
 * @author sxs
 */
public class WaterMarkInfo {

    /**
     * 水印旋转角度
     */
    private Integer degree = -30;
    /**
     * 水印透明度 值越小越透明
     */
    private Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f);

    /**
     * 水印与水印之间宽度偏移量
     */
    private int wordWidthOffset = 50;
    /**
     * 水印与水印高度偏移量
     */
    private int wordHeightOffset = 150;

    /**
     * 获取水印旋转角度
     *
     * @return 水印旋转角度，默认为-30度
     */
    public Integer getDegree() {
        return degree;
    }

    /**
     * 设置水印旋转角度
     *
     * @param degree 水印旋转角度
     * @return 当前对象，支持链式调用
     */
    public WaterMarkInfo setDegree(Integer degree) {
        this.degree = degree;
        return this;
    }

    /**
     * 获取水印透明度
     *
     * @return 水印透明度配置
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * 设置水印透明度
     *
     * @param composite 水印透明度配置
     * @return 当前对象，支持链式调用
     */
    public WaterMarkInfo setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    /**
     * 获取水印横向间距
     *
     * @return 水印之间的横向偏移量，默认为50像素
     */
    public int getWordWidthOffset() {
        return wordWidthOffset;
    }

    /**
     * 设置水印横向间距
     *
     * @param wordWidthOffset 水印之间的横向偏移量（像素）
     * @return 当前对象，支持链式调用
     */
    public WaterMarkInfo setWordWidthOffset(int wordWidthOffset) {
        this.wordWidthOffset = wordWidthOffset;
        return this;
    }

    /**
     * 获取水印纵向间距
     *
     * @return 水印之间的纵向偏移量，默认为150像素
     */
    public int getWordHeightOffset() {
        return wordHeightOffset;
    }

    /**
     * 设置水印纵向间距
     *
     * @param wordHeightOffset 水印之间的纵向偏移量（像素）
     * @return 当前对象，支持链式调用
     */
    public WaterMarkInfo setWordHeightOffset(int wordHeightOffset) {
        this.wordHeightOffset = wordHeightOffset;
        return this;
    }
}
