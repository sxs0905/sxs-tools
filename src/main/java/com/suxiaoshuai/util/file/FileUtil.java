package com.suxiaoshuai.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件操作工具类
 *
 * 提供文件操作的常用方法，包括：
 * <ul>
 *     <li>文件保存</li>
 *     <li>文件删除</li>
 * </ul>
 *
 * @author sxs
 * @since 1.0.0
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 保存文件到本地
     *
     * @param saveFilePath 文件本地保存路径
     * @param inputStream  输入流获取要保存的文件
     * @throws IOException IO异常
     */
    public static void saveFileToLocalPath(String saveFilePath, InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            //*****************************************
            // 创建文件
            File file = new File(saveFilePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            // 获取网络输入流
            bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buf = new byte[1024];
            int size = 0;
            // 保存文件
            while ((size = bufferedInputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, size);
            }
            //*****************************************
        } catch (Exception e) {
            throw new RuntimeException("保存文件到本地发生异常");
        } finally {
            if (null != bufferedInputStream) {
                bufferedInputStream.close();
            }
            if (null != fileOutputStream) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * 删除文件或目录
     * <p>
     * 如果是目录，会递归删除目录下的所有文件和子目录
     * </p>
     *
     * @param file 要删除的文件或目录
     */
    public static void deleteFile(File file) {
        try {
            if (null == file) {
                return;
            }
            if (!file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File value : files) {// 遍历目录下所有的文件
                    deleteFile(value);// 把每个文件用这个方法进行迭代
                }
            }
            file.delete();
        } catch (Exception e) {
            logger.error("delete file error", e);
        }
    }
}