package com.suxiaoshuai.util.image;

/**
 * 图片缩放模式枚举
 *
 * 定义了图片缩放的不同模式，包括原始尺寸、自定义尺寸、按宽度等比例缩放和按高度等比例缩放。
 */
public enum ScaleMode {
    /**
     * 原始尺寸
     */
    ORIGINAL,
    /**
     * 自定义
     */
    CUSTOMER,
    /**
     * 按宽度等比例缩放
     */
    FIXED_WIDTH,
    /**
     * 按高度等比例缩放
     */
    FIXED_HEIGHT,
}
