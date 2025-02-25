package com.suxiaoshuai.util.file;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * 文件路径分隔符
     */
    public static final String FILE_PATH_SPLIT_FLAG = "/";

    /**
     * 私有构造函数，防止实例化
     */
    private PropertiesUtil() {
    }

    /**
     * 将properties文件内容拷贝到另一个properties文件中
     *
     * @param originPropName    原始文件名
     * @param targetPropName    目标文件名
     * @param filePathSplitFlag 文件路径分隔符
     * @throws RuntimeException 如果复制过程中发生IO异常
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
        } catch (Exception e) {
            logger.error("copy properties error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据文件名加载classpath路径下的properties文件
     *
     * @param fileName          properties文件名
     * @param filePathSplitFlag 文件路径分隔符
     * @return 加载的Properties对象
     * @throws RuntimeException 如果加载过程中发生异常
     */
    public static Properties getClassPathProperties(String fileName, String filePathSplitFlag) {
        Properties runtimeProperties = new Properties();
        try {
            InputStream inputStream = getClassPathFileInputStream(fileName, filePathSplitFlag);
            runtimeProperties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("get classpath properties error", e);
            throw new RuntimeException(e);
        }
        return runtimeProperties;
    }

    /**
     * 根据文件名获取classpath下的properties文件对应的输入流
     *
     * @param fileName          properties文件名
     * @param filePathSplitFlag 路径分隔符用以获得classpath下路径
     * @return properties文件的输入流
     */
    private static InputStream getClassPathFileInputStream(String fileName, String filePathSplitFlag) {
        return PropertiesUtil.class.getResourceAsStream(filePathSplitFlag + fileName);
    }

    /**
     * 根据文件名获取classpath下的properties文件对应的输出流
     *
     * @param fileName          properties文件名
     * @param filePathSplitFlag 路径分隔符用以获得classpath
     * @return properties文件的输出流，如果文件不存在则返回null
     * @throws RuntimeException 如果获取输出流过程中发生异常
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
