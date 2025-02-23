package com.suxiaoshuai.util.system;

import com.suxiaoshuai.util.security.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 代表当前主机的信息。
 *
 * @author sxs
 */
public class HostInfo {


    private static final Logger logger = LoggerFactory.getLogger(HostInfo.class);

    private final String HOST_NAME;
    private final String HOST_ADDRESS;

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
     * 取得当前主机的名称。
     * <p>
     * <p>
     * 例如：<code>"webserver1"</code>
     * </p>
     *
     * @return 主机名
     */
    public final String getName() {
        return HOST_NAME;
    }

    /**
     * 取得当前主机的地址。
     * <p>
     * <p>
     * 例如：<code>"192.168.0.1"</code>
     * </p>
     *
     * @return 主机地址
     */
    public final String getAddress() {
        return HOST_ADDRESS;
    }

    /**
     * 将当前主机的信息转换成字符串。
     *
     * @return 主机信息的字符串表示
     */
    @Override
    public final String toString() {

        return "Host Name:    " + getName() +
                "Host Address: " + getAddress();
    }

}
