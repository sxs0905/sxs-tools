package com.suxiaoshuai.util.image;

/**
 * 水印排版配置
 * 对应前端界面中的：排列方向、排列顺序、对齐方式、元素间距、快速定位、边距与平铺开关
 */
public class WaterMarkLayoutOptions {

    /** 排列方向：横排或竖排 */
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    /** 排列顺序：图片在前或文字在前 */
    public enum Order {
        IMAGE_FIRST,
        TEXT_FIRST
    }

    /** 对齐方式：起始、居中、末尾 */
    public enum Align {
        START,
        CENTER,
        END
    }

    /** 快速定位：整体水印块在原图中的锚点位置 */
    public enum Position {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER
    }

    /** 排列方向 */
    private Direction direction = Direction.HORIZONTAL;
    /** 排列顺序 */
    private Order order = Order.TEXT_FIRST;
    /** 非主轴方向上的对齐方式 */
    private Align align = Align.CENTER;
    /** 图文元素间距（像素） */
    private int elementSpacing = 10;
    /** 水印整体快速定位 */
    private Position position = Position.BOTTOM_RIGHT;
    /** 水平边距（像素） */
    private int marginX = 20;
    /** 垂直边距（像素） */
    private int marginY = 20;
    /** 是否平铺水印 */
    private boolean tiled = false;

    public static WaterMarkLayoutOptions tiledDefault() {
        WaterMarkLayoutOptions options = new WaterMarkLayoutOptions();
        options.setTiled(true);
        return options;
    }

    public Direction getDirection() {
        return direction;
    }

    public WaterMarkLayoutOptions setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public WaterMarkLayoutOptions setOrder(Order order) {
        this.order = order;
        return this;
    }

    public Align getAlign() {
        return align;
    }

    public WaterMarkLayoutOptions setAlign(Align align) {
        this.align = align;
        return this;
    }

    public int getElementSpacing() {
        return elementSpacing;
    }

    public WaterMarkLayoutOptions setElementSpacing(int elementSpacing) {
        this.elementSpacing = Math.max(0, elementSpacing);
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public WaterMarkLayoutOptions setPosition(Position position) {
        this.position = position;
        return this;
    }

    public int getMarginX() {
        return marginX;
    }

    public WaterMarkLayoutOptions setMarginX(int marginX) {
        this.marginX = Math.max(0, marginX);
        return this;
    }

    public int getMarginY() {
        return marginY;
    }

    public WaterMarkLayoutOptions setMarginY(int marginY) {
        this.marginY = Math.max(0, marginY);
        return this;
    }

    public boolean isTiled() {
        return tiled;
    }

    public WaterMarkLayoutOptions setTiled(boolean tiled) {
        this.tiled = tiled;
        return this;
    }
}
