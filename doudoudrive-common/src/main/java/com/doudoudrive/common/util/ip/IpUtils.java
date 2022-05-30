package com.doudoudrive.common.util.ip;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.Region;
import com.doudoudrive.common.util.lang.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;

/**
 * <p>IP地址工具类集</p>
 * <p>2022-03-13 23:22</p>
 *
 * @author Dan
 **/
@Slf4j
public class IpUtils {

    /**
     * ip库文件名
     */
    private static final String IP2REGION = "ip2region.db";

    /**
     * ip库文件路径
     */
    private static final String IP2REGION_PATH = "data/" + IP2REGION;

    /**
     * ip库临时存储的文件路径
     */
    private static final String IP2REGION_TEMP_PATH = "logs/" + IP2REGION;

    /**
     * ip库的搜索方式-内存搜索
     */
    private static final String MEMORY_SEARCH = "memorySearch";

    /**
     * ip库的搜索方式- b-tree 算法
     */
    private static final String BTREE_SEARCH = "btreeSearch";

    /**
     * ip库的搜索方式- 二进制搜索 算法
     */
    private static final String BINARY_SEARCH = "binarySearch";

    /**
     * 默认情况下的内网IP显示的字符
     */
    private static final String INTRANET_IP = "内网IP";

    /**
     * 通过域名获取IP地址
     *
     * @param hostName 域名信息(注意不要加http或https,类似于cmd的ping命令)
     * @return 返回IP地址，如果未能获取到IP地址则返回域名信息hostName
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (java.net.UnknownHostException e) {
            return hostName;
        }
    }

    /**
     * 获取本机网卡的IP地址和计算机名
     *
     * @return 本机网卡的IP地址和计算机名，获取失败返回null
     */
    public static InetAddress getLocalhost() {
        return NetUtil.getLocalhost();
    }

    /**
     * 获取本机网卡IP地址，这个地址为所有网卡中非回路地址的第一个<br>
     * 如果获取失败调用 {@link InetAddress#getLocalHost()}方法获取。<br>
     * 此方法不会抛出异常，获取失败将返回null<br>
     *
     * @return 本机网卡IP地址，获取失败返回<code>null</code>
     */
    public static String getLocalhostStr() {
        return NetUtil.getLocalhostStr();
    }

    /**
     * 隐藏掉IP地址的最后一部分,使用 * 代替
     *
     * @param ip 需要操作的IP地址
     * @return 隐藏部分后的IP
     */
    public static String hideIpPart(String ip) {
        return NetUtil.hideIpPart(ip);
    }

    /**
     * 判断是否为内网IP
     *
     * @param ip IP地址
     * @return 返回是否为内网IP地址(true : 内网IP | | false : 不是内网IP)
     */
    public static boolean isInnerIp(String ip) {
        return NetUtil.isInnerIP(ip);
    }

    /**
     * 检测本地端口是否可用<br>
     *
     * @param port 需要被检测的端口
     * @return 返回给定的端口是否可用(true : 可用 | | false : 不可用)
     */
    public static boolean isUsableLocalPort(int port) {
        return NetUtil.isUsableLocalPort(port);
    }

    /**
     * 是否为有效的端口<br>
     * 此方法并不检查端口是否被占用
     * 有效端口是0～65535
     *
     * @param port 需要被检测的端口
     * @return 是否有效(true : 有效 | | false : 无效)
     */
    public static boolean isValidPort(int port) {
        return NetUtil.isValidPort(port);
    }

    /**
     * 获取指定IP地址的实际地理位置<br>
     * 使用内存搜索 memorySearch 算法
     *
     * @param ip 需要查询的IP地址信息
     * @return 返回查询到的地区信息，查询出错时返回null
     */
    public static Region getIpLocationByMemory(String ip) {
        return getIpLocation(ip, MEMORY_SEARCH);
    }

    /**
     * 获取指定IP地址的实际地理位置<br>
     * 使用 b-tree 算法
     *
     * @param ip 需要查询的IP地址信息
     * @return 返回查询到的地区信息，查询出错时返回null
     */
    public static Region getIpLocationByBtree(String ip) {
        return getIpLocation(ip, BTREE_SEARCH);
    }

    /**
     * 获取指定IP地址的实际地理位置<br>
     * 使用 二进制搜索算法 算法
     *
     * @param ip 需要查询的IP地址信息
     * @return 返回查询到的地区信息，查询出错时返回null
     */
    public static Region getIpLocationByBinary(String ip) {
        return getIpLocation(ip, BINARY_SEARCH);
    }

    /**
     * 获取指定IP地址的实际地理位置<br>
     *
     * @param ip        需要查询的IP地址信息
     * @param algorithm 查询算法(不同算法间有时间差异)
     * @return 返回查询到的地区信息，查询出错时返回null
     */
    private static synchronized Region getIpLocation(String ip, String algorithm) {
        // 判断ip是否为空
        if (StrUtil.isBlank(ip)) {
            log.info("The IP address detected is empty...");
            return new Region(INTRANET_IP, INTRANET_IP, INTRANET_IP, INTRANET_IP);
        }
        // 检测是否为局域网IP
        if (isInnerIp(ip)) {
            return new Region(INTRANET_IP, INTRANET_IP, INTRANET_IP, INTRANET_IP);
        }
        try {
            DbConfig config = Singleton.get(DbConfig.class);
            final File tempFile = FileUtil.touch(Singleton.get(File.class, IP2REGION_TEMP_PATH));
            if (CollectionUtil.isEmpty(tempFile)) {
                // 临时文件为空时，将包内文件流写入到文件
                try (InputStream inputStream = IpUtils.class.getClassLoader().getResourceAsStream(IP2REGION_PATH)) {
                    if (inputStream != null) {
                        FileUtils.copyInputStreamToFile(inputStream, tempFile);
                    }
                }
            }
            DbSearcher dbSearcher = Singleton.get(DbSearcher.class, config, tempFile.getPath());
            Method method = dbSearcher.getClass().getMethod(algorithm, String.class);
            DataBlock dataBlock = (DataBlock) method.invoke(dbSearcher, ip);
            if (dataBlock != null && StrUtil.isNotBlank(dataBlock.getRegion())) {
                String[] ipData = dataBlock.getRegion().split("\\|");
                return Region.builder()
                        .country(ipData[NumberConstant.INTEGER_ZERO])
                        .province(ipData[NumberConstant.INTEGER_TWO])
                        .city(ipData[NumberConstant.INTEGER_THREE])
                        .isp(ipData[NumberConstant.INTEGER_FOUR])
                        .build();
            } else {
                return new Region(INTRANET_IP, INTRANET_IP, INTRANET_IP, INTRANET_IP);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Region(INTRANET_IP, INTRANET_IP, INTRANET_IP, INTRANET_IP);
        }
    }
}
