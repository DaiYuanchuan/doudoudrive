package com.doudoudrive.sms.util;

import cn.hutool.core.util.RandomUtil;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.dto.SmsCache;

/**
 * <p>通讯平台常用工具类</p>
 * <p>2022-05-04 21:52</p>
 *
 * @author Dan
 **/
public class SmsUtil {

    /**
     * 通讯平台验证码信息发送，发送失败时抛出业务异常
     *
     * @param sendRecord         消息发送记录数据
     * @param throughput         最大吞吐量配置
     * @param cacheManagerConfig 框架服务缓存信息处理接口
     * @param smsManager         通讯平台通用业务处理层接口
     * @param recordService      SMS发送记录服务层接口
     */
    public static void verificationCode(SmsSendRecord sendRecord,
                                        Integer throughput, CacheManagerConfig cacheManagerConfig,
                                        SmsManager smsManager, SmsSendRecordService recordService) {
        // 生成4位数随机安全码
        String securityCode = RandomUtil.randomStringUpper(4);

        // 验证码缓存key
        String cacheKey = ConstantConfig.Cache.MAIL_VERIFICATION_CODE + sendRecord.getSmsRecipient();
        // 获取缓存中的值
        SmsCache cache = cacheManagerConfig.getCache(cacheKey);
        if (cache != null) {
            // 缓存的时间戳 > 当前的时间戳
            if (cache.getTimestamp() >= System.currentTimeMillis() || cache.getNumber() >= throughput) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.TOO_MANY_REQUESTS);
            }
        } else {
            cache = new SmsCache();
        }

        // 构建消息发送记录
        sendRecord.setSmsTitle(String.format("验证码：%s", securityCode));
        sendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.WAIT.status);
        SmsSendRecordModel sendRecordModel = recordService.insert(sendRecord);

        // 发送验证码
        smsManager.verificationCode(securityCode, sendRecordModel);

        // 构建验证码缓存对象
        cache.setNumber(cache.getNumber() + NumberConstant.INTEGER_ONE);
        cache.setData(securityCode);
        cache.setCreateTime(System.currentTimeMillis());
        // 获取当前时间偏移1分钟后的时间戳
        cache.setTimestamp(cache.getCreateTime() + ConstantConfig.DateUnit.MINUTE.ms);

        // 插入缓存，设置缓存有效期为1天
        cacheManagerConfig.putCache(cacheKey, cache, ConstantConfig.DateUnit.DAY.s);
    }

    /**
     * 校验验证码是否正确，校验失败时抛出业务异常
     *
     * @param recipient          需要校验的收件人信息
     * @param code               邮箱验证码
     * @param cacheManagerConfig 框架服务缓存信息处理接口
     */
    public static void verifyCode(String recipient, String code, CacheManagerConfig cacheManagerConfig) {
        // 验证码缓存key
        String cacheKey = ConstantConfig.Cache.MAIL_VERIFICATION_CODE + recipient;

        // 获取缓存中验证码的值
        SmsCache cacheData = cacheManagerConfig.getCache(cacheKey);
        if (cacheData == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.VERIFY_CODE_NOT_EXIST);
        }

        // 获取缓存插入时间偏移5分钟后的时间戳
        long validTime = cacheData.getCreateTime() + (NumberConstant.INTEGER_FIVE * ConstantConfig.DateUnit.MINUTE.ms);
        // 有效时间 < 当前的时间戳
        if (validTime < System.currentTimeMillis()) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.VERIFY_CODE_NOT_EXIST);
        }

        // 判断缓存中的值与给定的值是否一致
        if (!code.equalsIgnoreCase(cacheData.getData())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.VERIFY_CODE_INVALID);
        }
    }
}
