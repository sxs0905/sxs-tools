package com.suxiaoshuai.util.image;

import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.security.FormatUtil;
import com.suxiaoshuai.util.string.StringUtil;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.net.io.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片水印工具类
 * 提供对图片添加文字水印和图片水印的功能，支持水印的旋转、透明度、间距等属性设置
 *
 * @author sxs
 */
public class WaterMarkImageUtil_1 {

    private static final Logger logger = LoggerFactory.getLogger(WaterMarkImageUtil_1.class);

    /**
     * 本地文本水印
     *
     * @param oriFile       源文件路径
     * @param destFile      输出文件
     * @param waterMarkInfo 水印信息
     */
    public static void text(File oriFile, File destFile, TextWaterMark waterMarkInfo) {
        watermark(oriFile, destFile, null, waterMarkInfo);
    }

    /**
     * 本地图片水印
     *
     * @param oriFile       源文件路径
     * @param destFile      输出文件
     * @param waterMarkInfo 水印信息
     */
    public static void image(File oriFile, File destFile, ImageWaterMark waterMarkInfo) {
        watermark(oriFile, destFile, waterMarkInfo, null);
    }

    /**
     * 本地复合水印（图片水印+文字水印）
     *
     * @param oriFile       源文件路径
     * @param destFile      输出文件
     * @param imageWaterMark 图片水印信息，可为null
     * @param textWaterMark  文字水印信息，可为null
     */
    public static void watermark(File oriFile, File destFile, ImageWaterMark imageWaterMark, TextWaterMark textWaterMark) {
        watermark(oriFile, destFile, imageWaterMark, textWaterMark, WaterMarkLayoutOptions.tiledDefault());
    }

    /**
     * 本地复合水印（图片水印+文字水印），支持排版配置
     *
     * @param oriFile         源文件路径
     * @param destFile        输出文件
     * @param imageWaterMark  图片水印信息，可为null
     * @param textWaterMark   文字水印信息，可为null
     * @param layoutOptions   排版配置，null时使用默认配置
     */
    public static void watermark(File oriFile, File destFile, ImageWaterMark imageWaterMark, TextWaterMark textWaterMark,
                                 WaterMarkLayoutOptions layoutOptions) {
        try {
            validateExistingImageFile(oriFile);
            if (destFile != null) {
                validateOutputFile(destFile);
            }
            if (imageWaterMark == null && textWaterMark == null) {
                throw new SxsToolsException("图片水印和文字水印不能同时为空");
            }

            WaterMarkLayoutOptions options = layoutOptions == null ? new WaterMarkLayoutOptions() : layoutOptions;
            BufferedImage srcImage = ImageIO.read(oriFile);
            if (srcImage == null) {
                throw new SxsToolsException("源图片读取失败: " + oriFile.getAbsolutePath());
            }
            BufferedImage canvas = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = canvas.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(srcImage, 0, 0, null);
                renderCompositeWatermark(g, canvas.getWidth(), canvas.getHeight(), imageWaterMark, textWaterMark, options);
            } finally {
                g.dispose();
            }
            InputStream inputStream = toInputStream(canvas);
            out(oriFile, inputStream, destFile, "wm");
        } catch (Exception e) {
            logger.error("markImageByMulti error:{}", e.getMessage(), e);
        }
    }

    /**
     * 流式复合水印（图片水印+文字水印），适用于非本地文件场景
     * 调用方负责管理输入输出流生命周期
     *
     * @param oriInputStream  源图片输入流
     * @param destOutputStream 目标图片输出流
     * @param imageWaterMark  图片水印信息，可为null
     * @param textWaterMark   文字水印信息，可为null
     */
    public static void watermark(InputStream oriInputStream, OutputStream destOutputStream,
                                 ImageWaterMark imageWaterMark, TextWaterMark textWaterMark) {
        watermark(oriInputStream, destOutputStream, imageWaterMark, textWaterMark, WaterMarkLayoutOptions.tiledDefault());
    }

    /**
     * 流式复合水印（图片水印+文字水印），支持排版配置
     * 调用方负责管理输入输出流生命周期
     *
     * @param oriInputStream   源图片输入流
     * @param destOutputStream 目标图片输出流
     * @param imageWaterMark   图片水印信息，可为null
     * @param textWaterMark    文字水印信息，可为null
     * @param layoutOptions    排版配置，null时使用默认配置
     */
    public static void watermark(InputStream oriInputStream, OutputStream destOutputStream,
                                 ImageWaterMark imageWaterMark, TextWaterMark textWaterMark,
                                 WaterMarkLayoutOptions layoutOptions) {
        try {
            if (oriInputStream == null) {
                throw new SxsToolsException("源图片输入流不能为空");
            }
            if (destOutputStream == null) {
                throw new SxsToolsException("目标图片输出流不能为空");
            }
            if (imageWaterMark == null && textWaterMark == null) {
                throw new SxsToolsException("图片水印和文字水印不能同时为空");
            }

            WaterMarkLayoutOptions options = layoutOptions == null ? new WaterMarkLayoutOptions() : layoutOptions;
            BufferedImage srcImage = readImageFromInputStream(oriInputStream, "源图片");
            BufferedImage canvas = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = canvas.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(srcImage, 0, 0, null);
                renderCompositeWatermark(g, canvas.getWidth(), canvas.getHeight(), imageWaterMark, textWaterMark, options);
            } finally {
                g.dispose();
            }

            try (InputStream resultStream = toInputStream(canvas)) {
                copyToOutputStream(resultStream, destOutputStream);
            }
            destOutputStream.flush();
        } catch (Exception e) {
            logger.error("markImageByMultiStream error:{}", e.getMessage(), e);
            throw new SxsToolsException(e);
        }
    }

    /**
     * 字节数组复合水印（图片水印+文字水印），适用于MultipartFile/网络下载等非本地文件场景
     *
     * @param oriImageBytes  源图片字节数组
     * @param imageWaterMark 图片水印信息，可为null
     * @param textWaterMark  文字水印信息，可为null
     * @return 处理后的图片字节数组
     */
    public static byte[] watermark(byte[] oriImageBytes, ImageWaterMark imageWaterMark, TextWaterMark textWaterMark) {
        return watermark(oriImageBytes, imageWaterMark, textWaterMark, WaterMarkLayoutOptions.tiledDefault());
    }

    /**
     * 字节数组复合水印（图片水印+文字水印），支持排版配置
     *
     * @param oriImageBytes  源图片字节数组
     * @param imageWaterMark 图片水印信息，可为null
     * @param textWaterMark  文字水印信息，可为null
     * @param layoutOptions  排版配置，null时使用默认配置
     * @return 处理后的图片字节数组
     */
    public static byte[] watermark(byte[] oriImageBytes, ImageWaterMark imageWaterMark, TextWaterMark textWaterMark,
                                   WaterMarkLayoutOptions layoutOptions) {
        if (oriImageBytes == null || oriImageBytes.length == 0) {
            throw new SxsToolsException("源图片字节为空");
        }
        try (InputStream oriInputStream = new ByteArrayInputStream(oriImageBytes);
             ByteArrayOutputStream destOutputStream = new ByteArrayOutputStream()) {
            watermark(oriInputStream, destOutputStream, imageWaterMark, textWaterMark, layoutOptions);
            return destOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SxsToolsException("字节数组水印处理失败", e);
        }
    }

    /**
     * 便捷方法：传入源图字节和水印图字节，输出处理后的图片字节
     *
     * @param oriImageBytes       源图片字节数组
     * @param watermarkImageBytes 水印图片字节数组，可为null
     * @param textWaterMark       文字水印信息，可为null
     * @param layoutOptions       排版配置，null时使用默认配置
     * @return 处理后的图片字节数组
     */
    public static byte[] watermark(byte[] oriImageBytes, byte[] watermarkImageBytes,
                                   TextWaterMark textWaterMark, WaterMarkLayoutOptions layoutOptions) {
        ImageWaterMark imageWaterMark = null;
        if (watermarkImageBytes != null && watermarkImageBytes.length > 0) {
            imageWaterMark = new ImageWaterMark().setImageBytes(watermarkImageBytes);
        }
        return watermark(oriImageBytes, imageWaterMark, textWaterMark, layoutOptions);
    }

    private static void renderCompositeWatermark(Graphics2D g, int canvasWidth, int canvasHeight,
                                                 ImageWaterMark imageWaterMark, TextWaterMark textWaterMark,
                                                 WaterMarkLayoutOptions options) {
        List<RenderedElement> elements = buildElements(imageWaterMark, textWaterMark, options);
        if (elements.isEmpty()) {
            throw new SxsToolsException("没有可绘制的水印元素");
        }

        GroupLayout groupLayout = calculateGroupLayout(elements, options);
        if (options.isTiled()) {
            int stepX = Math.max(1, groupLayout.width + resolveTileOffsetX(imageWaterMark, textWaterMark));
            int stepY = Math.max(1, groupLayout.height + resolveTileOffsetY(imageWaterMark, textWaterMark));
            for (int y = -groupLayout.height / 2; y < canvasHeight + groupLayout.height; y += stepY) {
                for (int x = -groupLayout.width / 2; x < canvasWidth + groupLayout.width; x += stepX) {
                    drawGroup(g, elements, groupLayout, x, y);
                }
            }
            return;
        }

        Point origin = calculateOrigin(canvasWidth, canvasHeight, groupLayout.width, groupLayout.height, options);
        drawGroup(g, elements, groupLayout, origin.x, origin.y);
    }

    private static List<RenderedElement> buildElements(ImageWaterMark imageWaterMark, TextWaterMark textWaterMark,
                                                       WaterMarkLayoutOptions options) {
        List<RenderedElement> elements = new ArrayList<>();
        boolean imageFirst = options.getOrder() == WaterMarkLayoutOptions.Order.IMAGE_FIRST;
        if (imageFirst) {
            addImageElement(elements, imageWaterMark);
            addTextElement(elements, textWaterMark);
        } else {
            addTextElement(elements, textWaterMark);
            addImageElement(elements, imageWaterMark);
        }
        return elements;
    }

    private static void addImageElement(List<RenderedElement> elements, ImageWaterMark imageWaterMark) {
        if (imageWaterMark == null) {
            return;
        }
        BufferedImage image = loadAndScaleImageWatermark(imageWaterMark);
        if (image == null) {
            return;
        }
        int degree = imageWaterMark.getDegree() == null ? 0 : imageWaterMark.getDegree();
        elements.add(new RenderedElement(image, imageWaterMark.getComposite(), degree));
    }

    private static void addTextElement(List<RenderedElement> elements, TextWaterMark textWaterMark) {
        if (textWaterMark == null) {
            return;
        }
        String logoText = addTextSpace(textWaterMark.getText(), textWaterMark.getLetterSpacing());
        if (StringUtil.isBlank(logoText)) {
            return;
        }
        Font font = textWaterMark.getFont();
        FontMetrics fm = getFontMetrics(font);
        int textWidth = Math.max(1, fm.stringWidth(logoText));
        int textHeight = Math.max(1, fm.getHeight());
        BufferedImage textImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tg = textImage.createGraphics();
        try {
            tg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            tg.setFont(font);
            // 文字描边：对应界面中的“文字描边”开关、颜色和宽度
            GlyphVector glyphVector = font.createGlyphVector(tg.getFontRenderContext(), logoText);
            Shape textShape = glyphVector.getOutline(0, fm.getAscent());
            if (textWaterMark.isStrokeEnabled() && textWaterMark.getStrokeWidth() > 0f) {
                tg.setStroke(new BasicStroke(textWaterMark.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                tg.setColor(textWaterMark.getStrokeColor());
                tg.draw(textShape);
            }
            tg.setColor(textWaterMark.getColor());
            tg.fill(textShape);
        } finally {
            tg.dispose();
        }
        int degree = textWaterMark.getDegree() == null ? 0 : textWaterMark.getDegree();
        elements.add(new RenderedElement(textImage, textWaterMark.getComposite(), degree));
    }

    private static BufferedImage loadAndScaleImageWatermark(ImageWaterMark imageWaterMark) {
        Image watermarkImg = null;
        byte[] imageBytes = imageWaterMark.getImageBytes();
        if (imageBytes != null && imageBytes.length > 0) {
            watermarkImg = readWatermarkImage(imageBytes);
        }
        File watermarkFile = imageWaterMark.getImageFile();
        if (watermarkImg == null && watermarkFile != null) {
            watermarkImg = readWatermarkImage(watermarkFile);
        }
        if (watermarkImg == null) {
            ImageIcon imgIcon = imageWaterMark.getImageIcon();
            if (imgIcon != null) {
                watermarkImg = ensureImageLoaded(imgIcon);
            }
        }
        if (watermarkImg == null) {
            return null;
        }

        int originalWidth = watermarkImg.getWidth(null);
        int originalHeight = watermarkImg.getHeight(null);
        int finalWidth = originalWidth;
        int finalHeight = originalHeight;
        if (imageWaterMark.getScaleMode() == ScaleMode.FIXED_WIDTH && imageWaterMark.getWatermarkWidth() > 0) {
            finalWidth = imageWaterMark.getWatermarkWidth();
            finalHeight = (int) ((double) finalWidth / originalWidth * originalHeight);
        } else if (imageWaterMark.getScaleMode() == ScaleMode.FIXED_HEIGHT && imageWaterMark.getWatermarkHeight() > 0) {
            finalHeight = imageWaterMark.getWatermarkHeight();
            finalWidth = (int) ((double) finalHeight / originalHeight * originalWidth);
        } else if (imageWaterMark.getScaleMode() == ScaleMode.CUSTOMER
                && imageWaterMark.getWatermarkWidth() > 0 && imageWaterMark.getWatermarkHeight() > 0) {
            finalWidth = imageWaterMark.getWatermarkWidth();
            finalHeight = imageWaterMark.getWatermarkHeight();
        }

        if (finalWidth <= 0 || finalHeight <= 0) {
            return null;
        }
        BufferedImage scaled = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = scaled.createGraphics();
        try {
            ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            ig.drawImage(watermarkImg, 0, 0, finalWidth, finalHeight, null);
        } finally {
            ig.dispose();
        }
        return scaled;
    }

    private static GroupLayout calculateGroupLayout(List<RenderedElement> elements, WaterMarkLayoutOptions options) {
        int spacing = Math.max(0, options.getElementSpacing());
        int groupWidth = 0;
        int groupHeight = 0;
        if (options.getDirection() == WaterMarkLayoutOptions.Direction.HORIZONTAL) {
            for (int i = 0; i < elements.size(); i++) {
                RenderedElement element = elements.get(i);
                groupWidth += element.width;
                if (i > 0) {
                    groupWidth += spacing;
                }
                groupHeight = Math.max(groupHeight, element.height);
            }
        } else {
            for (int i = 0; i < elements.size(); i++) {
                RenderedElement element = elements.get(i);
                groupHeight += element.height;
                if (i > 0) {
                    groupHeight += spacing;
                }
                groupWidth = Math.max(groupWidth, element.width);
            }
        }
        return new GroupLayout(groupWidth, groupHeight, spacing, options.getDirection(), options.getAlign());
    }

    private static void drawGroup(Graphics2D g, List<RenderedElement> elements, GroupLayout layout, int originX, int originY) {
        int cursorX = 0;
        int cursorY = 0;
        int spacing = Math.max(0, layout.spacing);
        for (RenderedElement element : elements) {
            int x = originX;
            int y = originY;
            if (layout.direction == WaterMarkLayoutOptions.Direction.HORIZONTAL) {
                x += cursorX;
                y += alignOffset(layout.align, layout.height, element.height);
                cursorX += element.width + spacing;
            } else {
                x += alignOffset(layout.align, layout.width, element.width);
                y += cursorY;
                cursorY += element.height + spacing;
            }
            drawElement(g, element, x, y);
        }
    }

    private static void drawElement(Graphics2D g, RenderedElement element, int x, int y) {
        AffineTransform oldTransform = g.getTransform();
        Composite oldComposite = g.getComposite();
        try {
            if (element.composite != null) {
                g.setComposite(element.composite);
            }
            if (element.degree != 0) {
                g.rotate(Math.toRadians(element.degree), x + element.width / 2.0, y + element.height / 2.0);
            }
            g.drawImage(element.image, x, y, null);
        } finally {
            g.setComposite(oldComposite);
            g.setTransform(oldTransform);
        }
    }

    private static int alignOffset(WaterMarkLayoutOptions.Align align, int container, int content) {
        if (align == WaterMarkLayoutOptions.Align.START) {
            return 0;
        }
        if (align == WaterMarkLayoutOptions.Align.END) {
            return container - content;
        }
        return (container - content) / 2;
    }

    private static Point calculateOrigin(int canvasWidth, int canvasHeight, int groupWidth, int groupHeight,
                                         WaterMarkLayoutOptions options) {
        int marginX = Math.max(0, options.getMarginX());
        int marginY = Math.max(0, options.getMarginY());
        int x;
        int y;
        switch (options.getPosition()) {
            case TOP_LEFT:
                x = marginX;
                y = marginY;
                break;
            case TOP_RIGHT:
                x = canvasWidth - groupWidth - marginX;
                y = marginY;
                break;
            case BOTTOM_LEFT:
                x = marginX;
                y = canvasHeight - groupHeight - marginY;
                break;
            case CENTER:
                x = (canvasWidth - groupWidth) / 2;
                y = (canvasHeight - groupHeight) / 2;
                break;
            case BOTTOM_RIGHT:
            default:
                x = canvasWidth - groupWidth - marginX;
                y = canvasHeight - groupHeight - marginY;
                break;
        }
        return new Point(Math.max(0, x), Math.max(0, y));
    }

    private static int resolveTileOffsetX(ImageWaterMark imageWaterMark, TextWaterMark textWaterMark) {
        int imageOffset = imageWaterMark == null ? 0 : imageWaterMark.getWordWidthOffset();
        int textOffset = textWaterMark == null ? 0 : textWaterMark.getWordWidthOffset();
        int offset = Math.max(imageOffset, textOffset);
        return Math.max(50, offset);
    }

    private static int resolveTileOffsetY(ImageWaterMark imageWaterMark, TextWaterMark textWaterMark) {
        int imageOffset = imageWaterMark == null ? 0 : imageWaterMark.getWordHeightOffset();
        int textOffset = textWaterMark == null ? 0 : textWaterMark.getWordHeightOffset();
        int offset = Math.max(imageOffset, textOffset);
        return Math.max(120, offset);
    }

    private static InputStream toInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 给图片添加水印图片、可设置水印图片旋转角度
     *
     * @param oriImgStream      源图片IO流
     * @param iconWaterMarkInfo 水印信息，如果为null则使用默认配置
     * @return 添加水印后的图片输入流
     * @throws SxsToolsException 添加水印过程中发生异常
     */
    private static InputStream markImageByIcon(InputStream oriImgStream, ImageWaterMark iconWaterMarkInfo) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>> add water mark image begin");
        InputStream is;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {

            iconWaterMarkInfo = iconWaterMarkInfo == null ? new ImageWaterMark() : iconWaterMarkInfo;
            Integer degree = iconWaterMarkInfo.getDegree();
            Image srcImg = ImageIO.read(oriImgStream);
            // 图片宽度
            int width = srcImg.getWidth(null);
            // 图片高度
            int height = srcImg.getHeight(null);

            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // 1、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            buffImg = g.getDeviceConfiguration().createCompatibleImage(srcImg.getWidth(null), srcImg.getHeight(null), Transparency.TRANSLUCENT);
            g = buffImg.createGraphics();

            // 2、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null),
                    srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            // 3、设置水印旋转
            g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);

            // 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
            // 根据url生成ImageIcon,jar包内的图片相对应的文件不存在
            Image watermarkImg = null;
            File watermarkFile = iconWaterMarkInfo.getImageFile();
            if (watermarkFile != null) {
                watermarkImg = readWatermarkImage(watermarkFile);
            }

            if (watermarkImg == null) {
                ImageIcon imgIcon = iconWaterMarkInfo.getImageIcon();
                if (imgIcon != null) {
                    watermarkImg = ensureImageLoaded(imgIcon);
                }
            }

            if (watermarkImg != null) {

                int originalWidth = watermarkImg.getWidth(null);
                int originalHeight = watermarkImg.getHeight(null);
                int finalWidth = originalWidth;
                int finalHeight = originalHeight;

                if (iconWaterMarkInfo.getScaleMode() == ScaleMode.FIXED_WIDTH && iconWaterMarkInfo.getWatermarkWidth() > 0) {
                    finalWidth = iconWaterMarkInfo.getWatermarkWidth();
                    finalHeight = (int) ((double) finalWidth / originalWidth * originalHeight);
                } else if (iconWaterMarkInfo.getScaleMode() == ScaleMode.FIXED_HEIGHT && iconWaterMarkInfo.getWatermarkHeight() > 0) {
                    finalHeight = iconWaterMarkInfo.getWatermarkHeight();
                    finalWidth = (int) ((double) finalHeight / originalHeight * originalWidth);
                } else if (iconWaterMarkInfo.getScaleMode() == ScaleMode.CUSTOMER && iconWaterMarkInfo.getWatermarkWidth() > 0 && iconWaterMarkInfo.getWatermarkHeight() > 0) {
                    finalWidth = iconWaterMarkInfo.getWatermarkWidth();
                    finalHeight = iconWaterMarkInfo.getWatermarkHeight();
                }
                logger.info("water mark image origin width: {}, original height: {},final width:{},height:{}", originalWidth, originalHeight, finalWidth, finalHeight);
                if (finalWidth > 0 && finalHeight > 0) {
                    BufferedImage scaledWatermark = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D wG2d = scaledWatermark.createGraphics();
                    wG2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    wG2d.drawImage(watermarkImg, 0, 0, finalWidth, finalHeight, null);
                    wG2d.dispose();
                    watermarkImg = scaledWatermark;
                }

                g.setComposite(iconWaterMarkInfo.getComposite());

                // 获取文字的宽高
                // 文字的高度
                int iconHeight = watermarkImg.getHeight(null);
                // 文字的高度
                int iconWidth = watermarkImg.getWidth(null);
                // 文字在图片中的x坐标
                int x = -(iconWidth / 2);
                // 文字在图片中的y坐标，初始值向负方向偏移200，
                int y = -200;
                int drawCount = 0;
                while (height - y > -200) {
                    while (width - x > -100) {
                        g.drawImage(watermarkImg, x, y, null);
                        drawCount++;
                        x = x + iconWidth + iconWaterMarkInfo.getWordWidthOffset();
                    }
                    x = -(iconWidth / 2);
                    y = y + iconHeight + iconWaterMarkInfo.getWordHeightOffset();
                }

                // 偏移和旋转配置可能导致平铺区域未覆盖可视范围，兜底在中心点再绘制一次
                if (drawCount == 0) {
                    int centerX = (width - iconWidth) / 2;
                    int centerY = (height - iconHeight) / 2;
                    g.drawImage(watermarkImg, centerX, centerY, null);
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            } else {
                throw new SxsToolsException("图片水印未设置，请先调用setImageFile或setImageIcon");
            }
            // 7、释放资源
            g.dispose();

            // 8、生成图片
            byteArrayOutputStream = new ByteArrayOutputStream();
            // formatName需要为"png"，否则会失真，且图片的透明部分会变黑
            ImageIO.write(buffImg, "png", byteArrayOutputStream);
            is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> add water mark image finish");
        } catch (Exception e) {
            logger.error("add water mark image error:{}", e.getMessage(), e);
            throw new SxsToolsException(e);
        } finally {
            try {
                if (null != byteArrayOutputStream) {
                    byteArrayOutputStream.close();
                }
                if (null != oriImgStream) {
                    oriImgStream.close();
                }
            } catch (Exception e) {
                logger.error("add water mark image close stream error:{}", e.getMessage(), e);
            }
        }
        return is;
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     * 生成文件后缀为png
     *
     * @param inputStream   原始文件输入流
     * @param waterMarkInfo 水印信息，如果为null则使用默认配置
     * @return 添加水印后的图片输入流
     * @throws IOException 读写图片过程中发生IO异常
     */
    private static InputStream markImageByText(InputStream inputStream, TextWaterMark waterMarkInfo) throws IOException {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>> add water mark text begin");
        InputStream is;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            waterMarkInfo = waterMarkInfo == null ? new TextWaterMark() : waterMarkInfo;
            Integer degree = waterMarkInfo.getDegree();
            Font font = waterMarkInfo.getFont();
            String logoText = addTextSpace(waterMarkInfo.getText(), waterMarkInfo.getLetterSpacing());
            // 1、源图片
            Image srcImg = ImageIO.read(inputStream);
            int width = srcImg.getWidth(null);// 图片宽度
            int height = srcImg.getHeight(null);// 图片高度
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 保证Graphics2D画图时，图片的透明部分变黑
            buffImg = g.getDeviceConfiguration().createCompatibleImage(srcImg.getWidth(null), srcImg.getHeight(null), Transparency.TRANSLUCENT);
            g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            // 4、设置水印旋转
            g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            // 5、设置水印文字颜色
            g.setColor(waterMarkInfo.getColor());
            // 6、设置水印文字Font
            g.setFont(font);
            // 7、设置水印文字透明度
            g.setComposite(waterMarkInfo.getComposite());
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            // 获取文字的宽高
            FontMetrics fm = getFontMetrics(font);
            // 文字的高度
            int fontHeight = fm.getHeight();
            // 文字的高度
            int fontWidth = fm.stringWidth(logoText);
            // 文字在图片中的x坐标
            int x = -(fontWidth / 2);
            // 文字在图片中的y坐标，初始值向负方向偏移200，
            int y = -200;
            while (height - y > -200) {
                while (width - x > -100) {
                    g.drawString(logoText, x, y);
                    x = x + fontWidth + waterMarkInfo.getWordWidthOffset();
                }
                x = -(fontWidth / 2);
                y = y + fontHeight + waterMarkInfo.getWordHeightOffset();
            }
            // 9、释放资源
            g.dispose();
            // 10、生成图片
            byteArrayOutputStream = new ByteArrayOutputStream();
            // formatName需要为"png"，否则会失真，且图片的透明部分会变黑
            ImageIO.write(buffImg, "png", byteArrayOutputStream);
            is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            logger.info(">>>>>>>>>>>>>>>>>>>>>>> add water mark text finish");
        } catch (Exception e) {
            logger.error(" add water mark text error:{}", e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != byteArrayOutputStream) {
                    byteArrayOutputStream.close();
                }
            } catch (Exception e) {
                logger.error(" add water mark text close stream error:{}", e.getMessage(), e);
            }
        }
        return is;
    }

    /**
     * 在文本字符之间添加指定数量的空格
     *
     * @param text          原始文本
     * @param letterSpacing 字符间距（空格数量）
     * @return 添加了间距的文本，如果原文本为空或间距小于等于0则返回原文本
     */
    private static String addTextSpace(String text, int letterSpacing) {
        logger.info("add space text:{}", text);
        if (StringUtil.isBlank(text) || letterSpacing <= 0) {
            return text;
        }
        String blank = StringUtil.generatePlaceholder(FormatUtil.leftPad(0L, letterSpacing), ' ');
        StringBuilder sb = new StringBuilder();
        char[] charArray = text.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (i >= charArray.length - 1) {
                sb.append(charArray[i]);
            } else {
                sb.append(charArray[i]).append(blank);
            }
        }
        logger.info("add space text final:{}", sb);
        return sb.toString();
    }

    /**
     * 获取指定字体的度量信息
     *
     * @param font1 字体对象
     * @return 字体度量信息
     */
    private static FontMetrics getFontMetrics(Font font1) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        return g.getFontMetrics(font1);
    }

    /**
     * 确保ImageIcon对应的图片已加载完成，避免异步加载导致宽高为-1
     */
    private static Image ensureImageLoaded(ImageIcon imgIcon) {
        Image image = imgIcon.getImage();
        if (image == null) {
            return null;
        }
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SxsToolsException("等待水印图片加载时被中断", e);
        }
        if (tracker.isErrorID(0) || image.getWidth(null) <= 0 || image.getHeight(null) <= 0) {
            // 某些场景下ImageIcon可能仍未得到可用尺寸，尝试按描述路径同步读取一次
            String description = imgIcon.getDescription();
            String detectedType = "unknown";
            if (StringUtil.isNotBlank(description)) {
                File iconFile = new File(description);
                if (iconFile.exists() && iconFile.isFile()) {
                    try {
                        return readWatermarkImage(iconFile);
                    } catch (SxsToolsException e) {
                        detectedType = detectImageType(iconFile);
                        logger.warn("fallback load watermark image by description failed: {}", description, e);
                    }
                }
            }
            throw new SxsToolsException("水印图片未正确加载，请检查图片路径或图片内容，description=" + description
                    + ", detectedType=" + detectedType
                    + ", width=" + image.getWidth(null) + ", height=" + image.getHeight(null)
                    + "。可尝试改为setImageInputStream/setImageBytes/setImageFile");
        }
        return image;
    }

    /**
     * 读取水印图片，优先ImageIO，失败后使用commons-imaging兜底
     */
    private static BufferedImage readWatermarkImage(File imageFile) {
        if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
            throw new SxsToolsException("水印图片文件不存在: " + (imageFile == null ? "null" : imageFile.getAbsolutePath()));
        }
        try (InputStream in = new FileInputStream(imageFile)) {
            return readWatermarkImage(in.readAllBytes());
        } catch (IOException e) {
            throw new SxsToolsException("水印图片读取失败: " + imageFile.getAbsolutePath(), e);
        }
    }

    private static BufferedImage readWatermarkImage(byte[] imageBytes) {
        return decodeImageBytes(imageBytes, "水印图片");
    }

    private static BufferedImage readImageFromInputStream(InputStream inputStream, String imageName) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        return decodeImageBytes(bytes, imageName);
    }

    private static BufferedImage decodeImageBytes(byte[] imageBytes, String imageName) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new SxsToolsException(imageName + "字节为空");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (bufferedImage != null && bufferedImage.getWidth() > 0 && bufferedImage.getHeight() > 0) {
                return bufferedImage;
            }
        } catch (IOException e) {
            logger.warn("read watermark image by ImageIO failed", e);
        }

        try {
            BufferedImage bufferedImage = Imaging.getBufferedImage(imageBytes);
            if (bufferedImage != null && bufferedImage.getWidth() > 0 && bufferedImage.getHeight() > 0) {
                return bufferedImage;
            }
        } catch (Exception e) {
            logger.warn("read watermark image by commons-imaging failed", e);
        }

        if ("ico".equals(detectImageType(imageBytes))) {
            BufferedImage icoImage = decodeIcoImage(imageBytes);
            if (icoImage != null) {
                return icoImage;
            }
        }

        // 兜底：部分格式在ImageIO/Imaging下可能失败，尝试Awt图像加载链路（按字节）
        BufferedImage awtLoaded = loadImageByAwt(imageBytes);
        if (awtLoaded != null) {
            return awtLoaded;
        }

        throw new SxsToolsException(imageName + "读取失败，detectedType=" + detectImageType(imageBytes));
    }

    private static BufferedImage decodeIcoImage(byte[] imageBytes) {
        if (imageBytes.length < 6 || readUnsignedShortLe(imageBytes, 0) != 0
                || readUnsignedShortLe(imageBytes, 2) != 1) {
            return null;
        }

        int imageCount = readUnsignedShortLe(imageBytes, 4);
        BufferedImage bestImage = null;
        int bestScore = -1;
        for (int i = 0; i < imageCount; i++) {
            int entryOffset = 6 + i * 16;
            if (!hasBytes(imageBytes, entryOffset, 16)) {
                break;
            }

            IcoEntry entry = readIcoEntry(imageBytes, entryOffset);
            if (entry == null || !hasBytes(imageBytes, entry.imageOffset, entry.bytesInRes)) {
                continue;
            }

            BufferedImage decoded = decodeIcoEntry(imageBytes, entry);
            if (decoded == null) {
                continue;
            }
            int score = decoded.getWidth() * decoded.getHeight() * Math.max(1, entry.bitCount);
            if (score > bestScore) {
                bestScore = score;
                bestImage = decoded;
            }
        }
        return bestImage;
    }

    private static IcoEntry readIcoEntry(byte[] imageBytes, int entryOffset) {
        int width = imageBytes[entryOffset] & 0xFF;
        int height = imageBytes[entryOffset + 1] & 0xFF;
        int bitCount = readUnsignedShortLe(imageBytes, entryOffset + 6);
        long bytesInRes = readUnsignedIntLe(imageBytes, entryOffset + 8);
        long imageOffset = readUnsignedIntLe(imageBytes, entryOffset + 12);
        if (bytesInRes <= 0 || imageOffset < 0 || bytesInRes > Integer.MAX_VALUE || imageOffset > Integer.MAX_VALUE) {
            return null;
        }
        return new IcoEntry(width == 0 ? 256 : width, height == 0 ? 256 : height, bitCount,
                (int) bytesInRes, (int) imageOffset);
    }

    private static BufferedImage decodeIcoEntry(byte[] imageBytes, IcoEntry entry) {
        if (isPngAt(imageBytes, entry.imageOffset, entry.bytesInRes)
                || isJpegAt(imageBytes, entry.imageOffset, entry.bytesInRes)) {
            try {
                return ImageIO.read(new ByteArrayInputStream(imageBytes, entry.imageOffset, entry.bytesInRes));
            } catch (IOException e) {
                logger.warn("decode ico embedded image failed", e);
                return null;
            }
        }
        return decodeIcoDib(imageBytes, entry);
    }

    private static BufferedImage decodeIcoDib(byte[] imageBytes, IcoEntry entry) {
        int offset = entry.imageOffset;
        int length = entry.bytesInRes;
        if (length < 40 || !hasBytes(imageBytes, offset, 40)) {
            return null;
        }

        int headerSize = readIntLe(imageBytes, offset);
        if (headerSize < 40 || headerSize > length) {
            return null;
        }
        int dibWidth = readIntLe(imageBytes, offset + 4);
        int dibHeight = readIntLe(imageBytes, offset + 8);
        int bitCount = readUnsignedShortLe(imageBytes, offset + 14);
        int compression = readIntLe(imageBytes, offset + 16);
        if (dibWidth == 0 || dibHeight == 0 || compression != 0 || !isSupportedIcoBitCount(bitCount)) {
            return null;
        }

        int width = Math.abs(dibWidth);
        int height = entry.height > 0 ? entry.height : Math.abs(dibHeight) / 2;
        if (width <= 0 || height <= 0) {
            return null;
        }

        int colorCount = 0;
        if (bitCount <= 8) {
            int colorsUsed = readIntLe(imageBytes, offset + 32);
            colorCount = colorsUsed > 0 ? colorsUsed : 1 << bitCount;
        }

        int paletteOffset = offset + headerSize;
        int xorOffset = paletteOffset + colorCount * 4;
        int xorRowSize = ((width * bitCount + 31) / 32) * 4;
        int xorLength = xorRowSize * height;
        int maskOffset = xorOffset + xorLength;
        int maskRowSize = ((width + 31) / 32) * 4;
        boolean hasMask = hasBytes(imageBytes, maskOffset, maskRowSize * height);
        if (!hasBytes(imageBytes, xorOffset, xorLength)) {
            return null;
        }

        int[] palette = null;
        if (colorCount > 0) {
            if (!hasBytes(imageBytes, paletteOffset, colorCount * 4)) {
                return null;
            }
            palette = new int[colorCount];
            for (int i = 0; i < colorCount; i++) {
                int p = paletteOffset + i * 4;
                int b = imageBytes[p] & 0xFF;
                int g = imageBytes[p + 1] & 0xFF;
                int r = imageBytes[p + 2] & 0xFF;
                palette[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        boolean bottomUp = dibHeight > 0;
        boolean hasAlpha = false;
        for (int row = 0; row < height; row++) {
            int targetY = bottomUp ? height - 1 - row : row;
            int rowOffset = xorOffset + row * xorRowSize;
            for (int x = 0; x < width; x++) {
                int argb = readIcoPixel(imageBytes, rowOffset, x, bitCount, palette);
                if ((argb >>> 24) != 0) {
                    hasAlpha = true;
                }
                image.setRGB(x, targetY, argb);
            }
        }

        for (int row = 0; row < height; row++) {
            int targetY = bottomUp ? height - 1 - row : row;
            int maskRowOffset = maskOffset + row * maskRowSize;
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, targetY);
                boolean transparentByMask = hasMask && isMaskTransparent(imageBytes, maskRowOffset, x);
                if (transparentByMask) {
                    image.setRGB(x, targetY, argb & 0x00FFFFFF);
                } else if (!hasAlpha) {
                    image.setRGB(x, targetY, argb | 0xFF000000);
                }
            }
        }
        return image;
    }

    private static boolean isSupportedIcoBitCount(int bitCount) {
        return bitCount == 32 || bitCount == 24 || bitCount == 8 || bitCount == 4 || bitCount == 1;
    }

    private static int readIcoPixel(byte[] imageBytes, int rowOffset, int x, int bitCount, int[] palette) {
        if (bitCount == 32) {
            int pixelOffset = rowOffset + x * 4;
            int b = imageBytes[pixelOffset] & 0xFF;
            int g = imageBytes[pixelOffset + 1] & 0xFF;
            int r = imageBytes[pixelOffset + 2] & 0xFF;
            int a = imageBytes[pixelOffset + 3] & 0xFF;
            return (a << 24) | (r << 16) | (g << 8) | b;
        }
        if (bitCount == 24) {
            int pixelOffset = rowOffset + x * 3;
            int b = imageBytes[pixelOffset] & 0xFF;
            int g = imageBytes[pixelOffset + 1] & 0xFF;
            int r = imageBytes[pixelOffset + 2] & 0xFF;
            return 0xFF000000 | (r << 16) | (g << 8) | b;
        }
        if (bitCount == 8 && palette != null) {
            int paletteIndex = imageBytes[rowOffset + x] & 0xFF;
            return paletteIndex < palette.length ? palette[paletteIndex] : 0;
        }
        if (bitCount == 4 && palette != null) {
            int value = imageBytes[rowOffset + x / 2] & 0xFF;
            int paletteIndex = x % 2 == 0 ? (value >>> 4) & 0x0F : value & 0x0F;
            return paletteIndex < palette.length ? palette[paletteIndex] : 0;
        }
        if (bitCount == 1 && palette != null) {
            int value = imageBytes[rowOffset + x / 8] & 0xFF;
            int paletteIndex = (value >>> (7 - x % 8)) & 0x01;
            return paletteIndex < palette.length ? palette[paletteIndex] : 0;
        }
        return 0;
    }

    private static boolean isMaskTransparent(byte[] imageBytes, int maskRowOffset, int x) {
        int value = imageBytes[maskRowOffset + x / 8] & 0xFF;
        return ((value >>> (7 - x % 8)) & 0x01) == 1;
    }

    private static boolean isPngAt(byte[] bytes, int offset, int length) {
        return length >= 8 && hasBytes(bytes, offset, 8)
                && (bytes[offset] & 0xFF) == 0x89
                && bytes[offset + 1] == 0x50
                && bytes[offset + 2] == 0x4E
                && bytes[offset + 3] == 0x47
                && bytes[offset + 4] == 0x0D
                && bytes[offset + 5] == 0x0A
                && bytes[offset + 6] == 0x1A
                && bytes[offset + 7] == 0x0A;
    }

    private static boolean isJpegAt(byte[] bytes, int offset, int length) {
        return length >= 2 && hasBytes(bytes, offset, 2)
                && (bytes[offset] & 0xFF) == 0xFF
                && (bytes[offset + 1] & 0xFF) == 0xD8;
    }

    private static boolean hasBytes(byte[] bytes, int offset, int length) {
        return bytes != null && offset >= 0 && length >= 0 && offset <= bytes.length - length;
    }

    private static int readUnsignedShortLe(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8);
    }

    private static int readIntLe(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16)
                | (bytes[offset + 3] << 24);
    }

    private static long readUnsignedIntLe(byte[] bytes, int offset) {
        return readIntLe(bytes, offset) & 0xFFFFFFFFL;
    }

    private static void copyToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }

    private static BufferedImage loadImageByAwt(File imageFile) {
        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
        Image image = icon.getImage();
        if (image == null) {
            return null;
        }
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        if (tracker.isErrorID(0) || image.getWidth(null) <= 0 || image.getHeight(null) <= 0) {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            g2d.drawImage(image, 0, 0, null);
        } finally {
            g2d.dispose();
        }
        return bufferedImage;
    }

    private static BufferedImage loadImageByAwt(byte[] imageBytes) {
        Image image = Toolkit.getDefaultToolkit().createImage(imageBytes);
        if (image == null) {
            return null;
        }
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        if (tracker.isErrorID(0) || image.getWidth(null) <= 0 || image.getHeight(null) <= 0) {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            g2d.drawImage(image, 0, 0, null);
        } finally {
            g2d.dispose();
        }
        return bufferedImage;
    }

    /**
     * 根据文件头简单识别图片格式，用于诊断扩展名与真实格式不一致的情况
     */
    private static String detectImageType(File file) {
        try (InputStream in = new FileInputStream(file)) {
            byte[] header = new byte[12];
            int len = in.read(header);
            if (len >= 4) {
                if ((header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
                    return "png";
                }
                if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8) {
                    return "jpeg";
                }
                if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F') {
                    return "gif";
                }
                if (header[0] == 'B' && header[1] == 'M') {
                    return "bmp";
                }
                if (header[0] == 0x00 && header[1] == 0x00 && header[2] == 0x01 && header[3] == 0x00) {
                    return "ico";
                }
            }
            if (len >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                return "webp";
            }
        } catch (IOException e) {
            logger.warn("detect image type failed: {}", file.getAbsolutePath(), e);
        }
        return "unknown";
    }

    private static String detectImageType(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return "unknown";
        }
        if ((bytes[0] & 0xFF) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return "png";
        }
        if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) {
            return "jpeg";
        }
        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return "gif";
        }
        if (bytes[0] == 'B' && bytes[1] == 'M') {
            return "bmp";
        }
        if (bytes[0] == 0x00 && bytes[1] == 0x00 && bytes[2] == 0x01 && bytes[3] == 0x00) {
            return "ico";
        }
        if (bytes.length >= 12 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "webp";
        }
        return "unknown";
    }

    /**
     * 将输入流写入指定路径的文件
     *
     * @param oriFile     原始文件，用于生成默认的目标文件路径
     * @param inputStream 输入流
     * @param destFile    目标文件路径
     * @param fileEndName 文件后缀名，用于生成默认的目标文件路径
     * @throws IOException IO异常
     */
    private static void out(File oriFile, InputStream inputStream, File destFile, String fileEndName) throws IOException {
        if (destFile == null) {
            String parent = oriFile.getParent();
            String name = oriFile.getName();
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                String baseName = name.substring(0, dotIndex);
                String extension = name.substring(dotIndex);
                destFile = new File(parent, baseName + "_" + fileEndName + extension);
            } else {
                destFile = new File(parent, name + "_" + fileEndName);
            }
        }
        try (InputStream in = inputStream;
             OutputStream os = new FileOutputStream(destFile)) {
            Util.copyStream(in, os);
            os.flush();
        } catch (IOException e) {
            logger.error("write water mark image to dest file:{},stream to stream error:", destFile.toPath(), e);
            throw e;
        }
    }

    /**
     * 校验文件是否为图片文件
     *
     * @param file 待校验的文件
     * @throws SxsToolsException 如果文件不存在、不是文件或不是图片格式
     */
    private static void validateExistingImageFile(File file) {
        if (file == null) {
            throw new SxsToolsException("文件不能为空");
        }
        if (!file.exists() || !file.isFile()) {
            throw new SxsToolsException("文件不存在或不是文件: " + file.getAbsolutePath());
        }
        if (!isImageByContent(file)) {
            throw new SxsToolsException("文件不是可识别图片: " + file.getAbsolutePath());
        }
    }

    private static void validateOutputFile(File file) {
        if (file == null) {
            return;
        }
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            throw new SxsToolsException("输出目录不存在: " + parent.getAbsolutePath());
        }
        if (file.exists() && file.isDirectory()) {
            throw new SxsToolsException("输出路径不能是目录: " + file.getAbsolutePath());
        }
    }

    private static boolean isImageByContent(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null && image.getWidth() > 0 && image.getHeight() > 0) {
                return true;
            }
        } catch (IOException ignored) {
        }

        try {
            BufferedImage image = Imaging.getBufferedImage(file);
            if (image != null && image.getWidth() > 0 && image.getHeight() > 0) {
                return true;
            }
        } catch (Exception ignored) {
        }

        BufferedImage awtLoaded = loadImageByAwt(file);
        if (awtLoaded != null) {
            return true;
        }

        return !"unknown".equals(detectImageType(file));
    }

    private static final class RenderedElement {
        private final BufferedImage image;
        private final Composite composite;
        private final int degree;
        private final int width;
        private final int height;

        private RenderedElement(BufferedImage image, Composite composite, int degree) {
            this.image = image;
            this.composite = composite;
            this.degree = degree;
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }

    private static final class GroupLayout {
        private final int width;
        private final int height;
        private final int spacing;
        private final WaterMarkLayoutOptions.Direction direction;
        private final WaterMarkLayoutOptions.Align align;

        private GroupLayout(int width, int height, int spacing,
                            WaterMarkLayoutOptions.Direction direction,
                            WaterMarkLayoutOptions.Align align) {
            this.width = width;
            this.height = height;
            this.spacing = spacing;
            this.direction = direction;
            this.align = align;
        }
    }

    private static final class IcoEntry {
        private final int width;
        private final int height;
        private final int bitCount;
        private final int bytesInRes;
        private final int imageOffset;

        private IcoEntry(int width, int height, int bitCount, int bytesInRes, int imageOffset) {
            this.width = width;
            this.height = height;
            this.bitCount = bitCount;
            this.bytesInRes = bytesInRes;
            this.imageOffset = imageOffset;
        }
    }
}
