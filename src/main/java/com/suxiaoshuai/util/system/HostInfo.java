package com.suxiaoshuai.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 代表当前主机的信息。
 * 提供主机名称和IP地址的访问功能。
 *
 * @author sxs
 */
public class HostInfo {

    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(HostInfo.class);

    /** 主机名称 */
    private final String HOST_NAME;
    
    /** 主机IP地址 */
    private final String HOST_ADDRESS;

    /**
     * 构造函数，初始化主机信息。
     * 如果无法获取主机信息，将使用默认值：
     * 主机名："localhost"
     * IP地址："127.0.0.1"
     */
    public HostInfo() {
        String hostName = null;
        String hostAddress = null;

        try {
            InetAddress localhost = InetAddress.getLocalHost();

            hostName = localhost.getHostName();
            hostAddress = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("hostName:{},hostAddrerss:{},error:", hostName, hostAddress, e);
            hostName = "localhost";
            hostAddress = "127.0.0.1";
        }

        HOST_NAME = hostName;
        HOST_ADDRESS = hostAddress;
    }

    /**
     * 获取当前主机的名称。
     *
     * @return 主机名，例如：<code>"webserver1"</code>
     */
    public final String getName() {
        return HOST_NAME;
    }

    /**
     * 获取当前主机的IP地址。
     *
     * @return 主机IP地址，例如：<code>"192.168.0.1"</code>
     */
    public final String getAddress() {
        return HOST_ADDRESS;
    }

    /**
     * 将当前主机的信息转换成字符串表示形式。
     *
     * @return 包含主机名和IP地址的格式化字符串
     */
    @Override
    public final String toString() {
        return "Host Name:    " + getName() + "\n" +
               "Host Address: " + getAddress();
    }
}
