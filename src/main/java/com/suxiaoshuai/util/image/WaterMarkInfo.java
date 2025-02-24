package com.suxiaoshuai.util.image;

import java.awt.*;

/**
 * 水印信息
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

    public Integer getDegree() {
        return degree;
    }

    public WaterMarkInfo setDegree(Integer degree) {
        this.degree = degree;
        return this;
    }

    public Composite getComposite() {
        return composite;
    }

    public WaterMarkInfo setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    public int getWordWidthOffset() {
        return wordWidthOffset;
    }

    public WaterMarkInfo setWordWidthOffset(int wordWidthOffset) {
        this.wordWidthOffset = wordWidthOffset;
        return this;
    }

    public int getWordHeightOffset() {
        return wordHeightOffset;
    }

    public WaterMarkInfo setWordHeightOffset(int wordHeightOffset) {
        this.wordHeightOffset = wordHeightOffset;
        return this;
    }
}
