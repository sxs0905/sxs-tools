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
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片水印工具类
 * 提供对图片添加文字水印和图片水印的功能，支持水印的旋转、透明度、间距等属性设置
 *
 * @author sxs
 */
public class WaterMarkImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(WaterMarkImageUtil.class);

    private static final java.util.Set<String> IMAGE_EXTENSIONS = java.util.Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff", ".tif"
    );

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
        try {
            validateImageFile(oriFile);
            if (destFile != null) {
                validateImageFile(destFile);
            }
            if (imageWaterMark == null && textWaterMark == null) {
                throw new SxsToolsException("图片水印和文字水印不能同时为空");
            }

            InputStream inputStream = new FileInputStream(oriFile);
            if (imageWaterMark != null) {
                inputStream = markImageByIcon(inputStream, imageWaterMark);
            }
            if (textWaterMark != null) {
                inputStream = markImageByText(inputStream, textWaterMark);
            }
            out(oriFile, inputStream, destFile, "wm");
        } catch (Exception e) {
            logger.error("markImageByMulti error:{}", e.getMessage(), e);
        }
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
                    + "。可尝试改为setImageFile传入图片文件");
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
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            if (bufferedImage != null && bufferedImage.getWidth() > 0 && bufferedImage.getHeight() > 0) {
                return bufferedImage;
            }
        } catch (IOException e) {
            logger.warn("read watermark image by ImageIO failed: {}", imageFile.getAbsolutePath(), e);
        }

        try {
            BufferedImage bufferedImage = Imaging.getBufferedImage(imageFile);
            if (bufferedImage != null && bufferedImage.getWidth() > 0 && bufferedImage.getHeight() > 0) {
                return bufferedImage;
            }
        } catch (IOException e) {
            logger.warn("read watermark image by commons-imaging failed: {}", imageFile.getAbsolutePath(), e);
        }

        throw new SxsToolsException("水印图片读取失败: " + imageFile.getAbsolutePath() + ", detectedType=" + detectImageType(imageFile));
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
    private static void validateImageFile(File file) {
        if (file == null) {
            throw new SxsToolsException("文件不能为空");
        }
        String fileName = file.getName().toLowerCase(java.util.Locale.ROOT);
        boolean isImage = IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
        if (!isImage) {
            throw new SxsToolsException("文件不是图片格式: " + file.getAbsolutePath());
        }
    }
}
