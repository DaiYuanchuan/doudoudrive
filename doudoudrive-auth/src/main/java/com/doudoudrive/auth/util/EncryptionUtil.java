package com.doudoudrive.auth.util;

import cn.hutool.core.util.RandomUtil;
import com.doudoudrive.common.model.dto.model.SecretSaltingInfo;
import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;

/**
 * <p>密码加密包</p>
 * <p>2022-03-22 14:03</p>
 *
 * @author Dan
 **/
public class EncryptionUtil {

    /**
     * 默认的加密算法
     */
    public static final String DEFAULT_ALGORITHM = "SHA-512";

    /**
     * 加密具体方法，使用shiro提供的加密方法
     *
     * @param password 需要进行加密的明文密码
     * @param salt     随机的盐值(盐值可为用户ID)
     * @return 返回加密后的字符串
     */
    public static String digestEncodedPassword(final String password, String salt) {
        final ConfigurableHashService hashService = new DefaultHashService();
        hashService.setHashAlgorithmName(DEFAULT_ALGORITHM);
        // 加密次数
        hashService.setHashIterations(1024);
        final HashRequest request = new HashRequest.Builder()
                .setSalt(salt)
                .setSource(password)
                .build();
        return hashService.computeHash(request).toHex();
    }

    /**
     * 明文加盐处理
     *
     * @param plaintext 需要加盐的明文数据
     * @return 返回JOSNObject数据
     * {
     * "password": "加盐后的密文数据",
     * "salt": "盐值"
     * }
     */
    public static SecretSaltingInfo secretSalting(String plaintext) {
        // 生成随机盐值
        String salt = RandomUtil.randomString(32);
        // 构建返回数据
        return SecretSaltingInfo.builder()
                // 将原始密码加盐
                .password(digestEncodedPassword(plaintext, salt))
                .salt(salt)
                .build();
    }
}