package com.doudoudrive.common.util.ip;

import com.doudoudrive.common.model.dto.model.Region;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.net.InetAddress;

/**
 * <p>IP地址工具类单元测试集</p>
 * <p>2022-03-14 12:09</p>
 *
 * @author Dan
 **/
@Slf4j
public class IpUtilsTest {

    /**
     * 指定一个IP地址(江苏 南京 移动)
     */
    private static final String IP = "36.152.39.58";

    /**
     * 通过域名获取IP地址测试
     */
    @Test
    public void getIpByHostTest() {
        Assert.isTrue("127.0.0.1".equals(IpUtils.getIpByHost("localhost")), "获取域名地址异常！");
    }

    @Test
    public void getLocalhostTest() {
        InetAddress localhost = IpUtils.getLocalhost();
        Assert.notNull(localhost, "获取本地地址异常！");
    }

    @Test
    public void getLocalhostStrTest() {
        String localhost = IpUtils.getLocalhostStr();
        Assert.notNull(localhost, "获取本机网卡IP地址异常！");
    }

    @Test
    public void hideIpPartTest() {
        Assert.isTrue("36.152.39.*".equals(IpUtils.hideIpPart(IP)), "hideIpPart error！");
    }

    @Test
    public void isUsableLocalPortTest() {
        Assert.isTrue(!IpUtils.isUsableLocalPort(135), "isUsableLocalPort error !");
        Assert.isTrue(IpUtils.isUsableLocalPort(80), "isUsableLocalPort error !");
    }

    @Test
    public void isValidPortTest() {
        Assert.isTrue(IpUtils.isValidPort(65535), "isValidPort error !");
        Assert.isTrue(!IpUtils.isValidPort(65536), "isValidPort error !");
    }

    /**
     * 内存搜索 memorySearch 算法查找ip地址
     */
    @Test
    public void getIpLocationByMemoryTest() {
        resultComparison(IpUtils.getIpLocationByMemory(IP));
    }

    /**
     * 使用 b-tree 算法查找IP地址
     */
    @Test
    public void getIpLocationByBtreeTest() {
        resultComparison(IpUtils.getIpLocationByBtree(IP));
    }

    /**
     * 使用 二进制搜索算法 算法查找IP地址
     */
    @Test
    public void getIpLocationByBinaryTest() {
        resultComparison(IpUtils.getIpLocationByBinary(IP));
    }

    /**
     * IP地址结果集比较
     *
     * @param region 地区信息
     */
    private void resultComparison(Region region) {
        Region resultRegion = Region.builder()
                .country("中国")
                .province("江苏省")
                .city("南京市")
                .isp("移动")
                .build();
        Assert.isTrue(resultRegion.getCountry().equals(region.getCountry()), "country error !");
        Assert.isTrue(resultRegion.getProvince().equals(region.getProvince()), "province error !");
        Assert.isTrue(resultRegion.getCity().equals(region.getCity()), "city error !");
        Assert.isTrue(resultRegion.getIsp().equals(region.getIsp()), "isp error !");
    }
}
