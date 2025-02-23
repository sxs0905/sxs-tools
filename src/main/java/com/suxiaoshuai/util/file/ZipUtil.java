package com.suxiaoshuai.util.file;

import com.suxiaoshuai.constants.DatePatternConstant;
import com.suxiaoshuai.constants.FileConstant;
import com.suxiaoshuai.exception.SxsToolsException;
import com.suxiaoshuai.util.charset.CharsetUtil;
import com.suxiaoshuai.util.date.DateUtil;
import com.suxiaoshuai.util.string.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Han on 2017/9/14.
 */
public class ZipUtil {

    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    private static final String ZIP_FILE = FileConstant.FILE_NAME_SEPARATOR + FileConstant.FILE_SUFFIX_ZIP;

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourcePath 源文件
     * @param zipPath    压缩文件保存路径
     * @param fileName   压缩文件名
     * @param charSet    编码
     */
    public static void zip(String sourcePath, String zipPath, String fileName, String charSet) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            if (StringUtil.isBlank(sourcePath) || StringUtil.isBlank(zipPath)) {
                throw new SxsToolsException("zip file path not exist");
            }
            if (zipPath.contains(sourcePath)) {
                throw new SxsToolsException("生成的zip文件路径在待压缩文件目录下,无法完成压缩操作");
            }
            fileName = StringUtil.isBlank(fileName) ? DateUtil.formatDate(new Date(), DatePatternConstant.PURE_DATETIME_PATTERN) : fileName;
            File targetFileDirectory = new File(zipPath);
            if (!targetFileDirectory.exists()) {
                targetFileDirectory.mkdirs();
            }
            File[] files = targetFileDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    name = name.substring(0, name.lastIndexOf(".") == -1 ? name.length() : name.lastIndexOf("."));
                    if (fileName.equalsIgnoreCase(name)) {
                        fileName = fileName + "_" + DateUtil.formatDate(new Date(), DatePatternConstant.PURE_DATETIME_PATTERN);
                        break;
                    }
                }
            }
            fos = new FileOutputStream(zipPath + File.separator + fileName + ZIP_FILE);
            zos = new ZipOutputStream(fos);
            if (StringUtil.isBlank(charSet)) {
                charSet = CharsetUtil.UTF_8;
            }
            zos.setEncoding(charSet);// 此处修改字节码方式。
            writeZip(new File(sourcePath), "", zos);
        } catch (Exception e) {
            logger.error("zip source:{},zipPath:{},name:{} error", sourcePath, zipPath, fileName, e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                logger.error("close zos error", e);
            }
        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {// 处理文件夹
            parentPath += file.getName() + File.separator;
            File[] files = file.listFiles();
            // 空目录则创建当前目录
            if (files != null) {
                for (File f : files) {
                    writeZip(f, parentPath, zos);
                }
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry ze = new ZipEntry(parentPath + file.getName());
                zos.putNextEntry(ze);
                byte[] content = new byte[CACHE_SIZE];
                int len;
                while ((len = fis.read(content)) != -1) {
                    zos.write(content, 0, len);
                    zos.flush();
                }

            } catch (Exception e) {
                logger.error("write zip file error:{}", file.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 解压zip文件
     *
     * @param zipFilePath    待解压文件
     * @param targetFilePath 解压目录
     * @param charset        编码
     * @return 具体解压后的文件
     */
    public static List<File> unzip(File zipFilePath, String targetFilePath, String charset) throws Exception {
        if (zipFilePath == null || !zipFilePath.exists()) {
            throw new SxsToolsException("待解压文件不存在");
        }
        if (StringUtil.isBlank(targetFilePath)) {
            throw new SxsToolsException("文件[" + zipFilePath.getName() + "]解压后存放的路径不能为空");
        }
        if (StringUtil.isBlank(charset)) {
            charset = CharsetUtil.UTF_8;
        }
        ZipFile file = new ZipFile(zipFilePath, charset);
        Enumeration<? extends ZipEntry> en = file.getEntries();
        ZipEntry ze;
        List<File> files = new ArrayList<File>();
        while (en.hasMoreElements()) {
            ze = en.nextElement();
            File f = new File(targetFilePath, ze.getName());
            // 创建完整路径
            if (ze.isDirectory()) {
                f.mkdirs();
                continue;
            } else {
                f.getParentFile().mkdirs();
            }
            InputStream is = file.getInputStream(ze);
            OutputStream os = new FileOutputStream(f);
            IOUtils.copy(is, os, 2048);
            is.close();
            os.close();
            files.add(f);
        }
        file.close();
        return files;
    }
}
