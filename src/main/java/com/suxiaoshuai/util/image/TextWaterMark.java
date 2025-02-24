package com.suxiaoshuai.util.image;

import java.awt.*;

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

    public String getText() {
        return text;
    }

    public TextWaterMark setText(String text) {
        this.text = text;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public TextWaterMark setColor(Color color) {
        this.color = color;
        return this;
    }

    public Font getFont() {
        return font;
    }

    public TextWaterMark setFont(Font font) {
        this.font = font;
        return this;
    }

    public int getLetterSpacing() {
        return letterSpacing;
    }

    public TextWaterMark setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
        return this;
    }
}
