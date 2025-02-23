package com.suxiaoshuai.util.file;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * properties文件工具类
 *
 * @author sxs
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

//    private final static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    public static final String FILE_PATH_SPLIT_FLAG = "/";

    private PropertiesUtil() {
    }

    /**
     * 将properties文件内容拷贝到另一个properties文件中
     *
     * @param originPropName    原始文件名
     * @param targetPropName    目标文件名
     * @param filePathSplitFlag 文件路径分隔符
     */
    public static void copyProperties(String originPropName, String targetPropName, String filePathSplitFlag) {
        Properties originProperties = getClassPathProperties(originPropName, filePathSplitFlag);
        Properties targetProperties = new Properties();
        OutputStream outputStream = getClassPathFileOutputStream(targetPropName, filePathSplitFlag);

        try {
            // 将原始properties内的内容放在目标properties中
            Set<Map.Entry<Object, Object>> entries = originProperties.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                String key = String.valueOf(entry.getKey());
                targetProperties.put(key, String.valueOf(entry.getValue()));
            }
            // 写入文件内
            targetProperties.store(outputStream, "application copy from runtime");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据文件名加载classpath路径下的properties文件
     */
    public static Properties getClassPathProperties(String fileName, String filePathSplitFlag) {
        Properties runtimeProperties = new Properties();
        try {
            InputStream inputStream = getClassPathFileInputStream(fileName, filePathSplitFlag);
            runtimeProperties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return runtimeProperties;
    }

    /**
     * 根据文件名获取classpath下的properties文件对应的输入流
     *
     * @param fileName          properties文件名
     * @param filePathSplitFlag 路径分隔符用以获得classpath下路径
     */
    private static InputStream getClassPathFileInputStream(String fileName, String filePathSplitFlag) {
        return PropertiesUtil.class.getResourceAsStream(filePathSplitFlag + fileName);
    }

    /**
     * 根据文件名获取classpath下的properties文件对应的输出流用以将内容输出此文件内
     *
     * @param fileName          properties文件名
     * @param filePathSplitFlag 路径分隔符用以获得classpath
     */
    private static OutputStream getClassPathFileOutputStream(String fileName, String filePathSplitFlag) {

        try {
            URL resource = PropertiesUtil.class.getClassLoader().getResource(filePathSplitFlag + fileName);
            if (resource != null) {
                return new FileOutputStream(resource.getFile());
            }
            return null;
        } catch (Exception e) {
            logger.error("getClassPathFileOutputStream error", e);
            throw new RuntimeException(e);
        }
    }
}
