package com.suxiaoshuai.util.image;

import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.security.FormatUtil;
import com.suxiaoshuai.util.string.StringUtil;
import org.apache.commons.net.io.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 图片水印工具类
 * 提供对图片添加文字水印和图片水印的功能，支持水印的旋转、透明度、间距等属性设置
 *
 * @author sxs
 */
public class WaterMarkImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(WaterMarkImageUtil.class);

    /**
     * 本地文本水印
     *
     * @param originFilePath 源文件路径
     * @param destFilePath   输出路径
     * @param waterMarkInfo  水印信息
     */
    public static void localText(String originFilePath, String destFilePath, TextWaterMark waterMarkInfo) {
        try {
            File file = new File(originFilePath);
            InputStream inputStream = markImageByText(new FileInputStream(file), waterMarkInfo);
            out(inputStream, destFilePath);
        } catch (Exception e) {
            logger.error("markImageByText error:{}", e.getMessage(), e);
        }
    }

    /**
     * 本地图片水印
     *
     * @param originFilePath 源文件路径
     * @param destFilePath   输出路径
     * @param waterMarkInfo  水印信息
     */
    public static void localImage(String originFilePath, String destFilePath, ImageWaterMark waterMarkInfo) {
        try {
            File file = new File(originFilePath);
            InputStream inputStream = markImageByIcon(new FileInputStream(file), waterMarkInfo);
            out(inputStream, destFilePath);
        } catch (Exception e) {
            logger.error("markImageByText error:{}", e.getMessage(), e);
        }
    }

    /**
     * 给图片添加水印图片、可设置水印图片旋转角度
     *
     * @param inputStream   源图片IO流
     * @param waterMarkInfo 水印信息，如果为null则使用默认配置
     * @return 添加水印后的图片输入流
     * @throws SxsToolsException 添加水印过程中发生异常
     */
    public static InputStream markImageByIcon(InputStream inputStream, ImageWaterMark waterMarkInfo) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>> add water mark image begin");
        InputStream is;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {

            waterMarkInfo = waterMarkInfo == null ? new ImageWaterMark() : waterMarkInfo;
            Integer degree = waterMarkInfo.getDegree();
            Image srcImg = ImageIO.read(inputStream);
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
            ImageIcon imgIcon = waterMarkInfo.getImageIcon();
            if (imgIcon != null) {
                // 得到Image对象。
                Image watermarkImg = imgIcon.getImage();

                int originalWidth = watermarkImg.getWidth(null);
                int originalHeight = watermarkImg.getHeight(null);
                int finalWidth = originalWidth;
                int finalHeight = originalHeight;

                if (waterMarkInfo.getScaleMode() == ScaleMode.FIXED_WIDTH && waterMarkInfo.getWatermarkWidth() > 0) {
                    finalWidth = waterMarkInfo.getWatermarkWidth();
                    finalHeight = (int) ((double) finalWidth / originalWidth * originalHeight);
                } else if (waterMarkInfo.getScaleMode() == ScaleMode.FIXED_HEIGHT && waterMarkInfo.getWatermarkHeight() > 0) {
                    finalHeight = waterMarkInfo.getWatermarkHeight();
                    finalWidth = (int) ((double) finalHeight / originalHeight * originalWidth);
                } else if (waterMarkInfo.getScaleMode() == ScaleMode.CUSTOMER && waterMarkInfo.getWatermarkWidth() > 0 && waterMarkInfo.getWatermarkHeight() > 0) {
                    finalWidth = waterMarkInfo.getWatermarkWidth();
                    finalHeight = waterMarkInfo.getWatermarkHeight();
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

                g.setComposite(waterMarkInfo.getComposite());

                // 获取文字的宽高
                // 文字的高度
                int iconHeight = watermarkImg.getHeight(null);
                // 文字的高度
                int iconWidth = watermarkImg.getWidth(null);
                // 文字在图片中的x坐标
                int x = -(iconWidth / 2);
                // 文字在图片中的y坐标，初始值向负方向偏移200，
                int y = -200;
                while (height - y > -200) {
                    while (width - x > -100) {
                        g.drawImage(watermarkImg, x, y, null);
                        x = x + iconWidth + waterMarkInfo.getWordWidthOffset();
                    }
                    x = -(iconWidth / 2);
                    y = y + iconHeight + waterMarkInfo.getWordHeightOffset();
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
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
                if (null != inputStream) {
                    inputStream.close();
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
    public static InputStream markImageByText(InputStream inputStream, TextWaterMark waterMarkInfo) throws IOException {
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
                if (null != inputStream) {
                    inputStream.close();
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
     * 将输入流写入指定路径的文件
     *
     * @param inputStream  输入流
     * @param destFilePath 目标文件路径
     * @throws IOException IO异常
     */
    private static void out(InputStream inputStream, String destFilePath) throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get(destFilePath));
        Util.copyStream(inputStream, os);
        os.flush();
        os.close();
        inputStream.close();
    }
}
