package com.doudoudrive.common.cache.timer;

import com.doudoudrive.common.cache.RedisMessageSubscriber;
import com.doudoudrive.common.cache.RedisTemplateClient;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.RedisDelayedQueueEnum;
import com.doudoudrive.common.model.dto.model.DelayQueueMsg;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.lang.CompressionUtil;
import com.doudoudrive.common.util.lang.ProtostuffUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * <p>redis延迟队列实现</p>
 * <p>2023-05-23 15:53</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("redisDelayedQueue")
public class RedisDelayedQueueImpl implements RedisDelayedQueue, CommandLineRunner, RedisMessageSubscriber {

    /**
     * 推送到期任务到延迟队列
     *
     * <pre>
     *     1、Zrangebyscore返回有序集合中指定分数区间的成员列表。按分数值递增(从小到大)次序排列。
     *     2、expired获取从0到当前时间的到期元素，判断是否有到期元素
     *     3、循环元素，ipAirs是Lua编程语言中的一种迭代器函数，用于遍历数组或列表类型的数据结构。
     *       其中 i 表示当前元素的索引，v 表示当前元素的值
     *       ipAirs 函数只能遍历连续整数索引的数组或列表
     *     4、struct.unpack是Lua语言标准库中的一个函数，用于二进制数据解包为对应的 Lua 数据类型
     *       B：一个字节无符号整数；
     *       c0：一个以 null 结尾的字符串；
     *       L：一个 4 个字节的无符号整数；
     *       c0：同上，表示第二个以 null 结尾的字符串。
     *     5、rPush 用于向 Redis 列表的右侧（尾部）添加一个或多个元素
     *     6、lRem 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素
     *     7、zRem 命令用于移除有序集中的一个或多个成员，不存在的成员将被忽略
     *       unpack函数用于将一个数组或一个table解包成多个值
     *     8、zRange 命令用于返回有序集中，指定区间内的成员，
     *       WITHSCORES选项，将会把成员的分数值也一并返回
     *     9、v[1] ~= nil 表示 v 中的第一个元素，~= nil 不等于空
     * </pre>
     * <pre>
     *     local expired = redis.call('zrangebyscore', KEYS[2], 0, ARGV[1], 'limit', 0, ARGV[2]);
     *     if #expired > 0 then
     *       for i, v in ipairs(expired) do
     *         local randomId, value = struct.unpack('Bc0Lc0', v);
     *         redis.call('rpush', KEYS[1], value);
     *         redis.call('lrem', KEYS[3], 1, v);
     *       end;
     *       redis.call('zrem', KEYS[2], unpack(expired));
     *     end;
     *     local v = redis.call('zrange', KEYS[2], 0, 0, 'WITHSCORES');
     *     if v[1] ~= nil then
     *       return v[2];
     *     end
     *     return nil;
     * </pre>
     */
    private static final String PUSH_TASK = "local expiredValues = redis.call('zrangebyscore', KEYS[2], 0, ARGV[1], 'limit', 0, ARGV[2]); "
            + "if #expiredValues > 0 then "
            + "for i, v in ipairs(expiredValues) do "
            + "local randomId, value = struct.unpack('Bc0Lc0', v);"
            + "redis.call('rpush', KEYS[1], value);"
            + "redis.call('lrem', KEYS[3], 1, v);"
            + "end; redis.call('zrem', KEYS[2], unpack(expiredValues));"
            + "end; local v = redis.call('zrange', KEYS[2], 0, 0, 'WITHSCORES'); "
            + "if v[1] ~= nil then return v[2]; end return nil;";
    /**
     * 添加任务到延迟队列
     *
     * <pre>
     *     1、struct.pack 用于将数据打包成二进制格式的函数，和上面的struct.unpack('Bc0Lc0')对应
     *     2、zAdd 命令用于将一个或多个成员元素及其分数值加入到有序集当中
     *     3、rPush 用于向 Redis 列表的右侧（尾部）添加一个或多个元素
     *     4、zRange 命令用于返回有序集中，指定区间内的成员
     *     5、Publish 命令用于将信息发送到指定的频道
     * </pre>
     * <pre>
     *     local value = struct.pack('Bc0Lc0', string.len(ARGV[2]), ARGV[2], string.len(ARGV[3]), ARGV[3]);
     *     redis.call('zadd', KEYS[1], ARGV[1], value);
     *     redis.call('rpush', KEYS[2], value);
     *     local v = redis.call('zrange', KEYS[1], 0, 0);
     *     if v[1] == value then
     *       redis.call('publish', KEYS[3], ARGV[1]);
     *     end;
     * </pre>
     */
    private static final String OFFER_TASK = "local value = struct.pack('Bc0Lc0', string.len(ARGV[2]), ARGV[2], string.len(ARGV[3]), ARGV[3]);"
            + "redis.call('zadd', KEYS[1], ARGV[1], value);"
            + "redis.call('rpush', KEYS[2], value);"
            + "local v = redis.call('zrange', KEYS[1], 0, 0); if v[1] == value then "
            + "redis.call('publish', '%s', ARGV[3]); end;";
    /**
     * redis延迟队列
     */
    private static final String REDIS_DELAY_QUEUE = "REDIS_DELAY_QUEUE:{%s}";
    /**
     * redis超时任务队列
     */
    private static final String REDIS_DELAY_QUEUE_TIMEOUT = "REDIS_DELAY_QUEUE_TIMEOUT:{%s}";
    /**
     * Base64解码器
     */
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    /**
     * 序列化工具
     */
    private static final ProtostuffUtil<DelayQueueMsg> SERIALIZER = new ProtostuffUtil<>();
    /**
     * Redis客户端操作相关工具类
     */
    private RedisTemplateClient redisTemplateClient;
    /**
     * 时间轮定时器
     */
    private TimeWheelManager timeWheelManager;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    @Autowired
    public void setTimeWheelManager(TimeWheelManager timeWheelManager) {
        this.timeWheelManager = timeWheelManager;
    }

    /**
     * redis接收到的延迟任务
     *
     * @param message redis消息体
     * @param channel 当前消息体对应的通道
     */
    @Override
    public void receiveMessage(byte[] message, String channel) {
        for (RedisDelayedQueueEnum delayedQueueEnum : RedisDelayedQueueEnum.values()) {
            if (delayedQueueEnum.getChannelTopic().equals(channel)) {
                Optional.ofNullable((String) redisTemplateClient.getValueSerializer().deserialize(message)).ifPresent(element -> {
                    // 原始消息体
                    byte[] body = DECODER.decode(element);
                    // 字节解压缩为字节数组
                    byte[] bytes = CompressionUtil.decompressBytes(body);
                    // 反序列化为延迟队列的消息体
                    Optional.ofNullable(SERIALIZER.deserialize(bytes, DelayQueueMsg.class)).ifPresent(delayQueueMsg -> {
                        // 执行任务掉调度
                        scheduleTask(delayQueueMsg.getTopic(), delayQueueMsg.getExpireTime());
                    });
                });
            }
        }
    }

    @Override
    public void offer(RedisDelayedQueueEnum delayedQueue, long delay, TimeUnit timeUnit, Object body) {
        // 生成一个长度为 8 的字节数组
        byte[] random = new byte[NumberConstant.INTEGER_EIGHT];
        // 生成随机的字节序列
        ThreadLocalRandom.current().nextBytes(random);

        // 获取延迟时间
        long delayInMs = timeUnit.toMillis(delay);

        // 构建消息内容
        DelayQueueMsg delayQueueMsg = DelayQueueMsg.builder()
                .topic(delayedQueue.getTopic())
                .expireTime(System.currentTimeMillis() + delayInMs)
                .body(MessageBuilder.build(body))
                .build();

        // 压缩消息体
        byte[] bodyByte = CompressionUtil.compress(SERIALIZER.serialize(delayQueueMsg));

        // 超时队列
        String timeout = String.format(REDIS_DELAY_QUEUE_TIMEOUT, delayedQueue.getTopic());
        // 延迟队列
        String queueName = String.format(REDIS_DELAY_QUEUE, delayedQueue.getTopic());
        // 执行的lua脚本
        String script = String.format(OFFER_TASK, delayedQueue.getChannelTopic());
        redisTemplateClient.eval(script, List.of(timeout, queueName), null, delayQueueMsg.getExpireTime(), random, bodyByte);
    }

    @Override
    public void run(String... args) {
        // 初始化延迟队列任务
        for (RedisDelayedQueueEnum delayedQueue : RedisDelayedQueueEnum.values()) {
            pushTask(delayedQueue.getTopic());
        }
    }

    /**
     * 推送到期的任务到延迟队列
     *
     * @param topic 队列名称
     */
    private void pushTask(String topic) {
        String timeout = String.format(REDIS_DELAY_QUEUE_TIMEOUT, topic);
        String queueName = String.format(REDIS_DELAY_QUEUE, topic);
        // push task name
        List<String> pushQueueName = List.of(topic, timeout, queueName);

        try {
            Object result = redisTemplateClient.eval(PUSH_TASK, pushQueueName, String.class, System.currentTimeMillis(), 100);
            if (result != null) {
                scheduleTask(topic, Long.parseLong(result.toString()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            long startTime = System.currentTimeMillis() + NumberConstant.LONG_FIVE * NumberConstant.LONG_ONE_THOUSAND;
            scheduleTask(topic, startTime);
        }
    }

    /**
     * 延迟任务调度
     *
     * @param topic     队列名称
     * @param startTime 开始时间
     */
    private void scheduleTask(final String topic, final Long startTime) {
        long delay = startTime - System.currentTimeMillis();
        if (delay > NumberConstant.INTEGER_TEN) {
            timeWheelManager.newTimeout(timeout -> pushTask(topic), delay, TimeUnit.MILLISECONDS);
        } else {
            pushTask(topic);
        }
    }
}