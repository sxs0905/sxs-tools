package com.suxiaoshuai.util.system;


import com.suxiaoshuai.util.string.StringUtil;

import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * @author sxs
 */
public final class SystemUtil {

    /***** Java运行时环境信息 *****/
    /**
     * Java 运行时环境规范名称
     */
    public final static String SPECIFICATION_NAME = "java.specification.name";
    /**
     * Java 运行时环境版本
     */
    public final static String VERSION = "java.version";
    /**
     * Java 运行时环境规范版本
     */
    public final static String SPECIFICATION_VERSION = "java.specification.version";
    /**
     * Java 运行时环境供应商
     */
    public final static String VENDOR = "java.vendor";
    /**
     * Java 运行时环境规范供应商
     */
    public final static String SPECIFICATION_VENDOR = "java.specification.vendor";
    /**
     * Java 供应商的 URL
     */
    public final static String VENDOR_URL = "java.vendor.url";
    /**
     * Java 安装目录
     */
    public final static String HOME = "java.home";
    /**
     * 加载库时搜索的路径列表
     */
    public final static String LIBRARY_PATH = "java.library.path";
    /**
     * 默认的临时文件路径
     */
    public final static String TMPDIR = "java.io.tmpdir";
    /**
     * 要使用的 JIT 编译器的名称
     */
    public final static String COMPILER = "java.compiler";
    /**
     * 一个或多个扩展目录的路径
     */
    public final static String EXT_DIRS = "java.ext.dirs";

    /***** Java虚拟机信息 *****/
    /**
     * Java 虚拟机实现名称
     */
    public final static String VM_NAME = "java.vm.name";
    /**
     * Java 虚拟机规范名称
     */
    public final static String VM_SPECIFICATION_NAME = "java.vm.specification.name";
    /**
     * Java 虚拟机实现版本
     */
    public final static String VM_VERSION = "java.vm.version";
    /**
     * Java 虚拟机规范版本
     */
    public final static String VM_SPECIFICATION_VERSION = "java.vm.specification.version";
    /**
     * Java 虚拟机实现供应商
     */
    public final static String VM_VENDEOR = "java.vm.vendor";
    /**
     * Java 虚拟机规范供应商
     */
    public final static String VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";

    /***** Java类信息 *****/
    /**
     * Java 类格式版本号
     */
    public final static String CLASS_VERSION = "java.class.version";
    /**
     * Java 类路径
     */
    public final static String CLASS_PATH = "java.class.path";

    /***** OS信息 *****/
    /**
     * 操作系统的名称
     */
    public final static String OS_NAME = "os.name";
    /**
     * 操作系统的架构
     */
    public final static String OS_ARCH = "os.arch";
    /**
     * 操作系统的版本
     */
    public final static String OS_VERSION = "os.version";
    /**
     * 文件分隔符（在 UNIX 系统中是“/”）
     */
    public final static String FILE_SEPRATOR = "file.separator";
    /**
     * 路径分隔符（在 UNIX 系统中是“:”）
     */
    public final static String PATH_SEPRATOR = "path.separator";
    /**
     * 行分隔符（在 UNIX 系统中是“\n”）
     */
    public final static String LINE_SEPRATOR = "line.separator";

    /***** 用户信息 *****/
    /**
     * 用户的账户名称
     */
    public final static String USER_NAME = "user.name";
    /**
     * 用户的主目录
     */
    public final static String USER_HOME = "user.home";
    /**
     * 用户的当前工作目录
     */
    public final static String USER_DIR = "user.dir";

    private SystemUtil() {
    }

    //----------------------------------------------------------------------- Basic start

    /**
     * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在Log中，然后返回 <code>null</code>。
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值或<code>null</code>
     */
    public static String get(String name, String defaultValue) {
        return StringUtil.isBlank(get(name, false)) ? defaultValue : get(name, false);
    }

    /**
     * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在Log中，然后返回 <code>null</code>。
     *
     * @param name  属性名
     * @param quiet 安静模式，不将出错信息打在<code>System.err</code>中
     * @return 属性值或<code>null</code>
     */
    public static String get(String name, boolean quiet) {
        try {
            return System.getProperty(name);
        } catch (SecurityException e) {
            if (!quiet) {
            }
            return null;
        }
    }

    /**
     * 获得System属性（调用System.getProperty）
     *
     * @param key 键
     * @return 属性值
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * 获得boolean类型值
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }

        value = value.trim().toLowerCase();
        if (value.isEmpty()) {
            return true;
        }

        if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
            return true;
        }

        if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
            return false;
        }

        return defaultValue;
    }

    /**
     * @return 属性列表
     */
    public static Properties props() {
        return System.getProperties();
    }

    /**
     * 获取当前进程 PID
     *
     * @return 当前进程 ID
     */
    public static long getCurrentPID() {
        return Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }
    //----------------------------------------------------------------------- Basic end

}
