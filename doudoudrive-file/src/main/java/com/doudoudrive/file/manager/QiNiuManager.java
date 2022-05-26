package com.doudoudrive.file.manager;

/**
 * <p>七牛云相关服务通用业务处理层接口</p>
 * <p>2022-05-25 17:00</p>
 *
 * @author Dan
 **/
public interface QiNiuManager {

    /**
     * 生成HTTP七牛请求签名字符串
     *
     * @param urlString   url请求字符串
     * @param body        请求内容
     * @param contentType 请求类型
     * @return 签名字符串
     */
    String signRequest(String urlString, byte[] body, String contentType);

}
