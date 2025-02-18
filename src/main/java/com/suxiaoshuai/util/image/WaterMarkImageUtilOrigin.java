package com.suxiaoshuai.util.image;

import org.apache.commons.net.io.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * 给图片加水印工具类
 */
public class WaterMarkImageUtilOrigin {
    // 水印透明度
    private static float alpha = 1.0f;
    // 水印横向位置
    private static int positionWidth = 150;
    // 水印纵向位置
    private static int positionHeight = 300;
    // 水印文字字体
    private static Font font = new Font("Serif", Font.BOLD, 20);
    // 水印文字颜色
    private static Color color = Color.red;
    // 文字宽度偏移量,即文字与文字水平间隔
    private static int wordWidthOffset = 50;
    // 文字高度偏移量,即文字与文字垂直间隔
    private static int wordHeightOffset = 100;

    // 水印内容, 默认使用此内容
    private final static String LOGO_TEXT = "图 片 水 印";

    // 水印的地址
//    private static String DEFAULT_WATERMARK_ICON_PATH = null;


    /**
     * @param alpha          水印透明度
     * @param positionWidth  水印横向位置
     * @param positionHeight 水印纵向位置
     * @param font           水印文字字体
     * @param color          水印文字颜色
     */
    /*public static void setImageMarkOptions(float alpha, int positionWidth,
                                           int positionHeight, Font font, Color color) {
        if (alpha != 0.0f)
            WaterMarkImageUtil.alpha = alpha;
        if (positionWidth != 0)
            WaterMarkImageUtil.positionWidth = positionWidth;
        if (positionHeight != 0)
            WaterMarkImageUtil.positionHeight = positionHeight;
        if (font != null)
            WaterMarkImageUtil.font = font;
        if (color != null)
            WaterMarkImageUtil.color = color;
    }*/

    /**
     * 给图片添加水印图片
     *
     * @param inputStream 源图片IO流
     */
    public static InputStream markImageByIcon(InputStream inputStream) throws IOException {
        return markImageByIcon(null, inputStream, null, null);
    }

    /**
     * 给图片添加水印图片、可设置水印图片旋转角度
     *
     * @param iconPath    水印图片路径
     * @param inputStream 源图片IO流
     * @param degree      水印图片旋转角度
     * @param suffix      the suffix
     * @return the input stream
     * @throws IOException the io exception
     */
// 此处是根据图片生成图片水印，暂时不需要，以后需要时在座修改
    public static InputStream markImageByIcon(String iconPath, InputStream inputStream, Integer degree, String suffix) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            if (degree == null) {
                degree = -30;// 设置默认的旋转角度
            }
            /*if(iconPath == null){
                iconPath = DEFAULT_WATERMARK_ICON_PATH;
            }*/

            Image srcImg = ImageIO.read(inputStream);
            int width = srcImg.getWidth(null);// 图片宽度
            int height = srcImg.getHeight(null);// 图片高度

            BufferedImage buffImg = new BufferedImage(width,
                    height, BufferedImage.TYPE_INT_RGB);

            // 1、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            buffImg = g.getDeviceConfiguration().createCompatibleImage(srcImg.getWidth(null), srcImg.getHeight(null), Transparency.TRANSLUCENT);
            g = buffImg.createGraphics();

            // 2、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 3、设置水印旋转
            g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);

            // 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
//            ImageIcon imgIcon = new ImageIcon(iconPath);
            // 根据url生成ImageIcon,jar包内的图片相对应的文件不存在
            ImageIcon imgIcon = new ImageIcon(Objects.requireNonNull(WaterMarkImageUtil.class.getResource("/img/waterMarkIcon.png")));
//            ImageIcon imgIcon = new ImageIcon(WaterMarkImageUtil.class.getResource("/img/waterMarkIcon.png"));

            // 5、得到Image对象。
            Image img = imgIcon.getImage();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));

            // 6、水印图片的位置
//            g.drawImage(img, 0, 0, null);
//            g.drawImage(img, srcImg.getHeight(null) / 2, srcImg.getWidth(null) / 2, null);
            // 获取文字的宽高
            int iconHeight = img.getHeight(null);// 文字的高度
            int iconWidth = img.getWidth(null);// 文字的高度
            int x = -(iconWidth / 2);// 文字在图片中的x坐标
            int y = -200;// 文字在图片中的y坐标，初始值向负方向偏移200，
            while (height - y > -200) {
                while (width - x > -100) {
                    g.drawImage(img, x, y, null);
                    x = x + iconWidth + wordWidthOffset;
                }
                x = -(iconWidth / 2);
                y = y + iconHeight + wordHeightOffset;
            }
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            // 7、释放资源
            g.dispose();

            // 8、生成图片
            /*os = new FileOutputStream(targetPath);
            ImageIO.write(buffImg, "png", os);*/
            byteArrayOutputStream = new ByteArrayOutputStream();
            // write(@NotNull java.awt.image.RenderedImage im,@NotNull java.lang.String formatName,@NotNull java.io.OutputStream output)
            // formatName需要为"png"，否则会失真，且图片的透明部分会变黑
            ImageIO.write(buffImg, "png", byteArrayOutputStream);
            is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            System.out.println("图片完成添加水印图片");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != byteArrayOutputStream) {
                byteArrayOutputStream.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
        }
        return is;
    }

    /**
     * 给图片添加水印文字
     *
     * @param logoText    水印文字
     * @param inputStream 源图片路径
     * @param suffix      图片后缀
     */
    public static void markImageByText(String logoText, InputStream inputStream, String suffix) throws IOException {
        markImageByText(logoText, inputStream, null, suffix);
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     *
     * @param logoText    水印文字
     * @param inputStream 输入流
     * @param degree      水印旋转的角度
     * @param suffix      图片的后缀
     * @return InputStream input stream
     * @throws IOException the io exception
     */
    public static InputStream markImageByText(String logoText, InputStream inputStream, Integer degree, String suffix) throws IOException {

        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            String logoTextStr = logoText;
            if (logoTextStr == null) {
                logoTextStr = LOGO_TEXT;// 设置默认的水印
            }
            if (degree == null) {
                degree = -30;// 设置默认的旋转角度
            }
            // 1、源图片
            Image srcImg = ImageIO.read(inputStream);
            int width = srcImg.getWidth(null);// 图片宽度
            int height = srcImg.getHeight(null);// 图片高度
            BufferedImage buffImg = new BufferedImage(width,
                    height, BufferedImage.TYPE_INT_RGB);
            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 保证Graphics2D画图时，图片的透明部分变黑
            buffImg = g.getDeviceConfiguration().createCompatibleImage(srcImg.getWidth(null), srcImg.getHeight(null), Transparency.TRANSLUCENT);
            g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 4、设置水印旋转
            g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            // 5、设置水印文字颜色
            g.setColor(color);
            // 6、设置水印文字Font
            g.setFont(font);
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            // 获取文字的宽高
            FontMetrics fm = getFontMetrics(font);
            int fontHeight = fm.getHeight();// 文字的高度
            int fontWidth = fm.stringWidth(logoTextStr);// 文字的高度
            int x = -(fontWidth / 2);// 文字在图片中的x坐标
            int y = -200;// 文字在图片中的y坐标，初始值向负方向偏移200，
            while (height - y > -200) {
                while (width - x > -100) {
                    g.drawString(logoTextStr, x, y);
                    x = x + fontWidth + wordWidthOffset;
                }
                x = -(fontWidth / 2);
                y = y + fontHeight + wordHeightOffset;
            }
//            g.drawString(logoTextStr, width - fontWidth, height - fontHeight);
            // 9、释放资源
            g.dispose();
            // 10、生成图片
            // os = new FileOutputStream(targetPath);
            byteArrayOutputStream = new ByteArrayOutputStream();
            // write(@NotNull java.awt.image.RenderedImage im,@NotNull java.lang.String formatName,@NotNull java.io.OutputStream output)
            // formatName需要为"png"，否则会失真，且图片的透明部分会变黑
            ImageIO.write(buffImg, "png", byteArrayOutputStream);
            is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());


        } catch (Exception e) {
//            e.printStackTrace();
            throw e;
        } finally {

            if (null != byteArrayOutputStream) {
                byteArrayOutputStream.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
        }

        return is;
    }

    // 此处注释的代码为生成添加水印图片的代码
    /*public static InputStream markImageByText(String logoText, InputStream inputStream,
                                              String targetPath, Integer degree, String suffix) {

        InputStream is = null;
        OutputStream os = null;
        try {
            // 1、源图片
            Image srcImg = ImageIO.read(inputStream);
            int width = srcImg.getWidth(null);
            int height = srcImg.getHeight(null);
            BufferedImage buffImg = new BufferedImage(width,
                    height, BufferedImage.TYPE_INT_RGB);

            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 4、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 5、设置水印文字颜色
            g.setColor(color);
            // 6、设置水印文字Font
            g.setFont(font);
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
//            g.drawString(logoText, positionWidth, positionHeight);
            // 将水印放在右下角
            FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);// 获取文字的宽高
            g.drawString(logoText, width - fm.stringWidth(logoText), height - fm.getHeight());
            // 9、释放资源
            g.dispose();
            // 10、生成图片
            os = new FileOutputStream(targetPath);
            ImageIO.write(buffImg, suffix, os);

            System.out.println("图片完成添加水印文字");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }*/

    public static void aaa(InputStream inputStream, String targetPath) throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get(targetPath));
        Util.copyStream(inputStream, os);
        os.flush();
        os.close();
        inputStream.close();

    }

    public static void main(String[] args) throws IOException {
        String srcImgPath = "d:/1.jpg";
        String logoText = "此 图 片 仅 供 X X X X 内 部 使 用";
        String iconPath = "d:/2.jpg";

        String targerTextPath = "d:/qie_text.jpg";
        String targerTextPath2 = "d:/qie_text_rotate.jpg";

        String targerIconPath = "d:/qie_icon.jpg";
        String targerIconPath2 = "d:/qie_icon_rotate.jpg";

        System.out.println("给图片添加水印文字开始...");

//        File file = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Desert.jpg");
        /*File file = new File("D:\\b.jpg");
        InputStream is = markImageByText(null, new FileInputStream(file), -30, "png");
        aaa(is, targerTextPath);*/
        File file = new File("D:\\b.jpg");
        String aIconPath = Objects.requireNonNull(WaterMarkImageUtil.class.getResource("/img/waterMarkIcon.png")).getPath();
//        String aIconPath = "D:\\水印4.png";
        InputStream is = markImageByIcon(aIconPath, Files.newInputStream(file.toPath()), -30, null);
        aaa(is, targerIconPath);

        ImageIcon img = new ImageIcon("file:" + WaterMarkImageUtil.class.getResource("/img/waterMarkIcon.png").getPath().toString());
        System.out.println("args = [" + WaterMarkImageUtil.class.getResourceAsStream("/img/waterMarkIcon.png") + "]");

        // 给图片添加水印文字
//        markImageByText(logoText, srcImgPath, targerTextPath);
//        File file = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Image 1.png");
//        markImageByText(logoText, new FileInputStream(file));
//        markImageByText(LOGO_TEXT, new FileInputStream(file), targerTextPath, null, "png");
//        markImageByText(logoText, new FileInputStream(file), null, "png");
        // 给图片添加水印文字,水印文字旋转-45
//        markImageByText(logoText, srcImgPath, targerTextPath2, -45);
//        System.out.println("给图片添加水印文字结束...");

//        System.out.println("给图片添加水印图片开始...");
//        setImageMarkOptions(0.3f, 1, 1, null, null);
        // 给图片添加水印图片
//        markImageByIcon(iconPath, srcImgPath, targerIconPath);
        // 给图片添加水印图片,水印图片旋转-45
//        markImageByIcon(iconPath, srcImgPath, targerIconPath2, -45);
//        System.out.println("给图片添加水印图片结束...");

    }

    private static FontMetrics getFontMetrics(Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        return g.getFontMetrics(font);
    }
}
