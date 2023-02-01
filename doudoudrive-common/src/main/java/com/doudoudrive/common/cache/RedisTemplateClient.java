package com.doudoudrive.common.cache;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>Redis客户端操作相关工具类</p>
 * <p>2022-04-17 17:20</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class RedisTemplateClient {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void delete(String... key) {
        if (CollectionUtil.isNotEmpty(key)) {
            if (key.length == NumberConstant.INTEGER_ONE) {
                redisTemplate.delete(key[NumberConstant.INTEGER_ZERO]);
            } else {
                redisTemplate.delete(CollectionUtil.toList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取指定key对应value的以秒为单位的过期时间
     *
     * @param key 键
     * @return 以秒为单位的过期时间，如果 key 不存在时返回 -2，没有设置过期时间时返回 -1
     */
    public Long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取指定key对应value的以秒为单位的过期时间
     *
     * @param key      键
     * @param timeUnit 时间单位，秒、毫秒
     * @return 以秒为单位的过期时间，如果 key 不存在或没有设置过期时间，则返回 -1
     */
    public Long ttl(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public void set(String key, Object value, Long time) {
        if (time == null || time <= NumberConstant.INTEGER_ZERO) {
            set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        }
    }

    /**
     * 使用scan命令 查询某些前缀的key
     *
     * @param key 带有前缀的key
     * @return 值
     */
    public Set<String> scan(String key) {
        return this.redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(key)
                    .count(NumberConstant.INTEGER_ONE_THOUSAND).build());
            while (cursor.hasNext()) {
                binaryKeys.add(new String(cursor.next()));
            }
            return binaryKeys;
        });
    }

    /**
     * 使用scan命令 查询某些前缀的key 有多少个
     * 用来获取当前session数量,也就是在线用户
     *
     * @param key 带有前缀的key
     * @return key的数量
     */
    public Long scanSize(String key) {
        return this.redisTemplate.execute((RedisCallback<Long>) connection -> {
            long count = 0L;
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(key)
                    .count(NumberConstant.INTEGER_ONE_THOUSAND).build());
            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
            return count;
        });
    }

    /**
     * Redis Publish 命令用于将信息发送到指定的频道
     *
     * @param channel redis通道名称枚举值，用于将消息发送到指定频道
     * @param message 需要发送的消息
     */
    public void publish(ConstantConfig.Cache.ChanelEnum channel, Object message) {
        redisTemplate.convertAndSend(channel.getChannel(), message);
    }

    /**
     * 返回RedisTemplate模板使用的值序列化器
     *
     * @return 此模板使用的值序列化器
     */
    public RedisSerializer<?> getValueSerializer() {
        return redisTemplate.getValueSerializer();
    }

    /**
     * setNX key value 命令用于设置指定 key 的值
     * 只在键 key 不存在的情况下， 将键 key 的值设置为 value
     *
     * @param key   键
     * @param value 值
     * @return 成功返回1(true)，失败返回0(false)
     */
    public Boolean setNx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * setNX key value 命令用于设置指定 key 的值，同时设置过期时间
     * 只在键 key 不存在的情况下， 将键 key 的值设置为 value
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 成功返回1(true)，失败返回0(false)
     */
    public Boolean setNx(String key, Object value, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * incrBy key increment 命令，原子性操作将 key 中储存的数字值增加 increment
     *
     * @param key   键
     * @param delta 增量，incrBy命令允许此值为负数
     * @return 增加后的值
     */
    public Long increment(String key, Long delta) {
        if (delta == null) {
            // 当增量为null时，使用incr命令，增量默认为1
            return redisTemplate.opsForValue().increment(key);
        }
        // 当增量不为null时，使用incrBy命令
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * decrBy key decrement 命令，原子性操作将 key 中储存的数字值减少 decrement
     *
     * @param key   键
     * @param delta 减量，decrBy命令允许此值为负数
     * @return 减少后的值
     */
    public Long decrement(String key, Long delta) {
        if (delta == null) {
            // 当增量为null时，使用incr命令，增量默认为1
            return redisTemplate.opsForValue().decrement(key);
        }
        // 当增量不为null时，使用incrBy命令
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 该命令的作用是将 Lua 脚本交给 Redis 服务器执行，这个命令的语法如下：
     * <pre>
     *     eval script numKeys key [key ...] arg [arg ...]
     *     该命令的第一个参数`script`是要执行的Lua脚本的内容
     *     第二个参数`numKeys`是 Lua 脚本中使用的键的数量
     *     第三个参数`key`是 Lua 脚本中将要使用的键
     *     第四个参数`arg`是 Lua 脚本中将要使用的参数
     * </pre>
     * <p>
     * 使用该命令时需要注意以下几点：
     * <pre>
     *     1. 脚本执行的时间会被限制在 5 秒之内，如果脚本执行时间超过 5 秒，会返回超时的错误信息。
     *     2. 脚本的大小需要小于 512KB，否则会返回超过大小限制的错误信息。
     *     3. 脚本中使用的键的数量需要小于 10000 个，否则会返回超过数量限制的错误信息。
     *     4. 会将脚本中使用的键的值加载到脚本的环境中，并执行脚本，最后返回脚本的结果。
     *     5. 执行脚本的过程是单线程的，这意味着在脚本执行期间，其他客户端的操作会被挂起，因此要尽量避免在脚本中执行耗时操作。
     *     6. 在脚本中可以使用一些特殊的变量，这些变量由 Redis 内部使用，可以用来访问键的值和参数
     *     例如，可以使用 KEYS 变量来访问脚本中使用的所有键，使用 ARGV 变量来访问脚本中使用的所有参数。
     * </pre>
     *
     * @param script     要执行的lua脚本
     * @param keys       需要传递给脚本的键值
     * @param resultType 返回值的类型
     * @param args       需要传递给脚本的参数
     * @return 脚本的返回值，如果{@code resultType}为null，则脚本的返回值为null
     */
    public <T> T eval(String script, List<String> keys, Class<T> resultType, Object... args) {
        return redisTemplate.execute(RedisScript.of(script, resultType), keys, args);
    }
}
