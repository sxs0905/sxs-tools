package com.suxiaoshuai.util.image;

import java.awt.*;

/**
 * 文字水印信息类
 * 
 * 用于配置图片文字水印的相关属性，包括水印文本内容、颜色、字体和间距等
 */
public final class TextWaterMark extends WaterMarkInfo {
    /**
     * 水印内容
     */
    private String text = "WaterMark";
    /**
     * 水印颜色
     */
    private Color color = Color.LIGHT_GRAY;
    /**
     * 水印字体
     * Font.PLAIN（普通）
     * Font.BOLD（加粗）
     * Font.ITALIC（斜体）
     * Font.BOLD+ Font.ITALIC（粗斜体）
     * ...
     */
    private Font font = new Font("Serif", Font.ITALIC, 20);

    /**
     * 单个水印字体之间间隔
     */
    private int letterSpacing = 0;

    /**
     * 获取水印文本内容
     *
     * @return 水印文本内容
     */
    public String getText() {
        return text;
    }

    /**
     * 设置水印文本内容
     *
     * @param text 水印文本内容
     * @return 当前对象，支持链式调用
     */
    public TextWaterMark setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * 获取水印颜色
     *
     * @return 水印颜色
     */
    public Color getColor() {
        return color;
    }

    /**
     * 设置水印颜色
     *
     * @param color 水印颜色
     * @return 当前对象，支持链式调用
     */
    public TextWaterMark setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * 获取水印字体
     *
     * @return 水印字体
     */
    public Font getFont() {
        return font;
    }

    /**
     * 设置水印字体
     *
     * @param font 水印字体
     * @return 当前对象，支持链式调用
     */
    public TextWaterMark setFont(Font font) {
        this.font = font;
        return this;
    }

    /**
     * 获取字体间距
     *
     * @return 字体间距像素值
     */
    public int getLetterSpacing() {
        return letterSpacing;
    }

    /**
     * 设置字体间距
     *
     * @param letterSpacing 字体间距像素值
     * @return 当前对象，支持链式调用
     */
    public TextWaterMark setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
        return this;
    }
}
