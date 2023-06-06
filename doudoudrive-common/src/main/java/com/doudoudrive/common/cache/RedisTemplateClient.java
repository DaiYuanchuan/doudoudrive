package com.doudoudrive.common.cache;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * lPush 命令将一个或多个值插入到列表数据的头部（左边），这个命令的语法如下：
     * <pre>
     *     lPush key value [value ...]
     *     其中，key表示列表的键名，value表示要插入的一个或多个值。
     *     例如：lPush myList "!" "world" "hello"
     *     如果列表"myList"不存在，则创建一个新的列表并插入值，如果列表"myList"已经存在，则将值插入到其头部。
     *     如果一个键名对应的值不是一个列表，那么Redis会返回一个错误信息
     * </pre>
     *
     * @param key   键
     * @param value 值
     * @return 返回列表的长度
     */
    public Long leftPush(String key, Object... value) {
        if (CollectionUtil.isNotEmpty(value)) {
            if (value.length == NumberConstant.INTEGER_ONE) {
                return redisTemplate.opsForList().leftPush(key, value[NumberConstant.INTEGER_ZERO]);
            } else {
                return redisTemplate.opsForList().leftPushAll(key, value);
            }
        }
        return NumberConstant.LONG_ZERO;
    }

    /**
     * rPush命令用于将一个或多个值插入到列表的尾部（右边），这个命令的语法如下：
     * <pre>
     *     rPush key value [value ...]
     *     其中，key表示列表的键名，value表示要插入的一个或多个值。
     *     例如：rPush myList "hello" "world" "!"
     *     如果列表"myList"不存在，则创建一个新的列表并插入值，如果列表"myList"已经存在，则将值插入到其尾部。
     *     如果一个键名对应的值不是一个列表，那么Redis会返回一个错误信息
     * </pre>
     *
     * @param key   键
     * @param value 值
     * @return 返回列表的长度
     */
    public Long rightPush(String key, Object... value) {
        if (CollectionUtil.isNotEmpty(value)) {
            if (value.length == NumberConstant.INTEGER_ONE) {
                return redisTemplate.opsForList().rightPush(key, value[NumberConstant.INTEGER_ZERO]);
            } else {
                return redisTemplate.opsForList().rightPushAll(key, value);
            }
        }
        return NumberConstant.LONG_ZERO;
    }

    /**
     * lPOP 命令用于从列表的左侧（头部）移除并返回一个元素，这个命令的语法如下：
     * <pre>
     *     lPop key
     *     其中，key表示列表的键名。
     *     该命令会从给定的列表中移除并返回列表左侧的第一个元素
     *     如果列表不存在或者为空，那么该命令会返回一个nil值。
     *     例如：
     *       假设有一个名为 myList 的列表，包含以下元素：["a", "b", "c"]。
     *       使用 lPop 命令可以从列表的左侧移除并返回元素。
     *       lPop myList
     *       执行上述命令后，将会返回值 "a"，并且列表变为 ["b", "c"]。
     *     lPop 命令可以用于实现先进先出（FIFO）队列，以及其他需要按顺序处理元素的场景。
     * </pre>
     *
     * @param key 键
     * @return 返回列表的头部元素，如果列表不存在或者为空，那么该命令会返回一个nil值
     */
    public Object leftPop(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * lPOP 命令用于从列表的左侧（头部）移除并返回一个元素，这个命令的语法如下：
     * <pre>
     *     lPop key [count]
     *     其中，key表示列表的键名，count表示每次从列表中弹出元素的数量
     *     该命令会从给定的列表中移除并返回列表左侧的第一个元素
     *     如果列表不存在或者为空，那么该命令会返回一个nil值。
     *     例如：
     *       假设有一个名为 myList 的列表，包含以下元素：["a", "b", "c"]。
     *       使用 lPop 命令可以从列表的左侧移除并返回元素。
     *       lPop myList
     *       执行上述命令后，将会返回值 "a"，并且列表变为 ["b", "c"]。
     *     lPop 命令可以用于实现先进先出（FIFO）队列，以及其他需要按顺序处理元素的场景。
     * </pre>
     *
     * @param key   键
     * @param count 移除的个数
     * @return 返回列表的头部元素，如果列表不存在或者为空，那么该命令会返回一个nil值
     */
    public List<Object> leftPop(String key, long count) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return redisTemplate.opsForList().leftPop(key, count);
    }

    /**
     * blPOP 是一个阻塞列表命令，用于从一个或多个列表的左侧（头部）弹出一个元素。<br/>
     * 如果列表为空，blPOP 会阻塞当前客户端连接，直到有元素可用或达到指定的超时时间。
     * <pre>
     *     blPop key [key ...] timeout
     *     其中，key表示一个或多个列表的键名，timeout表示阻塞的超时时间，单位为秒。
     *     如果列表为空，客户端将在超时时间内阻塞，直到有元素可用。
     *     该命令会按照给定的键名顺序依次检查列表，并返回第一个非空列表的元素。
     *     如果所有列表都为空，则客户端将会阻塞，直到有元素可用或超时时间到达。
     *     例如：
     *       假设有两个列表 list1 和 list2，执行以下 blPop 命令：
     *       blPop list1 list2 10
     *       上述命令会检查 list1 和 list2，如果其中有任何一个列表不为空，则返回列表的第一个元素
     *       如果两个列表都为空，客户端将会阻塞 10 秒钟，直到有元素可用或超时时间到达。<br/>
     *     blPop 命令会将元素从列表中移除，并返回移除的元素。
     *     如果只是想查看列表中的元素而不移除，可以使用 lIndex 命令
     *     blPop 常用于实现队列的消费者模型，可以实现阻塞等待队列中的任务，并在任务可用时立即处理
     * </pre>
     *
     * @param key     键
     * @param timeout 超时时间，为0表示一直阻塞
     * @param unit    时间单位
     * @return 返回列表的头部元素，如果列表为空，那么该命令会阻塞当前客户端连接
     */
    public Object leftPop(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return redisTemplate.opsForList().leftPop(key, timeout, unit);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * sAdd命令用于向集合(Set)中添加一个或多个成员，这个命令的语法如下：
     * <pre>
     *     sAdd key member [member ...]
     *     key表示集合的键名，member表示要添加的一个或多个成员
     *     例如：sAdd mySet "hello" "world" "!"
     *     集合"mySet"不存在，则会创建一个新的集合并添加成员，如果集合"mySet"已经存在，则添加成员到集合中
     *     如果一个键名对应的值不是一个集合，那么Redis会返回一个错误信息
     * </pre>
     *
     * @param key   键
     * @param value 值
     * @return 返回添加到集合中的新成员的数量，不包括已经存在于集合中的成员
     */
    public Long sAdd(String key, Object... value) {
        if (CollectionUtil.isNotEmpty(value)) {
            return redisTemplate.opsForSet().add(key, value);
        }
        return NumberConstant.LONG_ZERO;
    }

    /**
     * sisMember命令用于判断一个值是否在集合(Set)中，这个命令的语法如下：
     * <pre>
     *     sisMember key member
     *     其中，key表示集合的键名，member表示要判断的值
     *     例如：sisMember mySet "hello"
     *     如果值"hello"在集合"mySet"中，则返回1，否则返回0。
     *     如果集合"mySet"不存在，则返回0。
     * </pre>
     *
     * @param key   键
     * @param value 值
     * @return true:存在，false:不存在
     */
    public Boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
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
        RedisScript<T> redisScript = resultType == null ? RedisScript.of(script) : RedisScript.of(script, resultType);
        return redisTemplate.execute(redisScript, keys, args);
    }
}
