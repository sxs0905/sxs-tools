package com.suxiaoshuai.util.system;


/**
 * 代表当前运行的JRE的信息。
 * 提供对JRE系统属性的访问功能。
 *
 * @author sxs
 */
public class JavaRuntimeInfo {

    /** JRE名称 */
    private final String JAVA_RUNTIME_NAME = SystemUtil.get("java.runtime.name", false);
    
    /** JRE版本 */
    private final String JAVA_RUNTIME_VERSION = SystemUtil.get("java.runtime.version", false);
    
    /** Java安装目录 */
    private final String JAVA_HOME = SystemUtil.get("java.home", false);
    
    /** Java扩展目录 */
    private final String JAVA_EXT_DIRS = SystemUtil.get("java.ext.dirs", false);
    
    /** Java信任目录 */
    private final String JAVA_ENDORSED_DIRS = SystemUtil.get("java.endorsed.dirs", false);
    
    /** Java类路径 */
    private final String JAVA_CLASS_PATH = SystemUtil.get("java.class.path", false);
    
    /** Java类版本 */
    private final String JAVA_CLASS_VERSION = SystemUtil.get("java.class.version", false);
    
    /** Java库路径 */
    private final String JAVA_LIBRARY_PATH = SystemUtil.get("java.library.path", false);

    /** Java启动类路径 */
    private final String SUN_BOOT_CLASS_PATH = SystemUtil.get("sun.boot.class.path", false);

    /** JVM数据模型（32位或64位） */
    private final String SUN_ARCH_DATA_MODEL = SystemUtil.get("sun.arch.data.model", false);

    /**
     * 获取JVM启动类路径
     *
     * @return 启动类路径，如果不能获取则返回null
     */
    public final String getSunBoothClassPath() {
        return SUN_BOOT_CLASS_PATH;
    }

    /**
     * 获取JVM数据模型
     *
     * @return "32"表示32位JVM，"64"表示64位JVM
     */
    public final String getSunArchDataModel() {
        return SUN_ARCH_DATA_MODEL;
    }

    /**
     * 获取当前JRE的名称
     *
     * @return JRE名称，例如：<code>"Java(TM) 2 Runtime Environment, Standard Edition"</code>
     * @since Java 1.3
     */
    public final String getName() {
        return JAVA_RUNTIME_NAME;
    }

    /**
     * 获取当前JRE的版本
     *
     * @return JRE版本，例如：<code>"1.4.2-b28"</code>
     * @since Java 1.3
     */
    public final String getVersion() {
        return JAVA_RUNTIME_VERSION;
    }

    /**
     * 取得当前JRE的安装目录（取自系统属性：<code>java.home</code>）。
     *
     * 例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/jre"</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     * @since Java 1.1
     */
    public final String getHomeDir() {
        return JAVA_HOME;
    }

    /**
     * 取得当前JRE的扩展目录列表（取自系统属性：<code>java.ext.dirs</code>）。
     *
     * 例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/jre/lib/ext:..."</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     * @since Java 1.3
     */
    public final String getExtDirs() {
        return JAVA_EXT_DIRS;
    }

    /**
     * 取得当前JRE的endorsed目录列表（取自系统属性：<code>java.endorsed.dirs</code>）。
     *
     * 例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/jre/lib/endorsed:..."</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     * @since Java 1.4
     */
    public final String getEndorsedDirs() {
        return JAVA_ENDORSED_DIRS;
    }

    /**
     * 取得当前JRE的系统classpath（取自系统属性：<code>java.class.path</code>）。
     *
     * 例如：<code>"/home/admin/myclasses:/home/admin/..."</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     * @since Java 1.1
     */
    public final String getClassPath() {
        return JAVA_CLASS_PATH;
    }

    /**
     * 取得当前JRE的class文件格式的版本（取自系统属性：<code>java.class.version</code>）。
     *
     * 例如Sun JDK 1.4.2：<code>"48.0"</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     * @since Java 1.1
     */
    public final String getClassVersion() {
        return JAVA_CLASS_VERSION;
    }

    /**
     * 取得当前JRE的library搜索路径（取自系统属性：<code>java.library.path</code>）。
     *
     * 例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/bin:..."</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     */
    public final String getLibraryPath() {
        return JAVA_LIBRARY_PATH;
    }

    /**
     * 取得当前JRE的library搜索路径（取自系统属性：<code>java.library.path</code>）。
     * 例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/bin:..."</code>
     * </p>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     */
    /*public final String[] getLibraryPathArray() {
        return StrUtil.split(getLibraryPath(), SystemUtil.get("path.separator", false));
    }*/

    /**
     * 取得当前JRE的URL协议packages列表（取自系统属性：<code>java.protocol.handler.pkgs</code>）。
     *
     * 例如Sun JDK 1.4.2：<code>"sun.net.www.protocol|..."</code>
     *
     * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
     */
    public final String getProtocolPackages() {
        return SystemUtil.get("java.protocol.handler.pkgs", true);
    }

    /**
     * 将当前运行的JRE信息转换成字符串。
     *
     * @return JRE信息的字符串表示
     */
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Java Runtime Name:      ").append(getName()).append("\n");
        builder.append("Java Runtime Version:   ").append(getVersion()).append("\n");
        builder.append("Java Home Dir:          ").append(getHomeDir()).append("\n");
        builder.append("Java Extension Dirs:    ").append(getExtDirs()).append("\n");
        builder.append("Java Endorsed Dirs:     ").append(getEndorsedDirs()).append("\n");
        builder.append("Java Class Path:        ").append(getClassPath()).append("\n");
        builder.append("Java Class Version:     ").append(getClassVersion()).append("\n");
        builder.append("Java Library Path:      ").append(getLibraryPath()).append("\n");
        builder.append("Java Protocol Packages: ").append(getProtocolPackages());

        return builder.toString();
    }
}
