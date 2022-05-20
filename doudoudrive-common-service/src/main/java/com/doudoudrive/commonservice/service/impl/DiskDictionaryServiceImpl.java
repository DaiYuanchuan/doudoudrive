package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.cache.RedisMessageSubscriber;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.DiskDictionary;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ConvertUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.DiskDictionaryDao;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>数据字典模块服务层实现</p>
 * <p>2022-04-07 20:10:02</p>
 *
 * @author Dan
 */
@Scope("singleton")
@Service("diskDictionaryService")
@DataSource(DataSourceEnum.CONFIG)
public class DiskDictionaryServiceImpl implements DiskDictionaryService, RedisMessageSubscriber, CommandLineRunner {

    private DiskDictionaryDao diskDictionaryDao;

    @Autowired
    public void setDiskDictionaryDao(DiskDictionaryDao diskDictionaryDao) {
        this.diskDictionaryDao = diskDictionaryDao;
    }

    /**
     * 系统字典数据缓存
     */
    private static final Map<String, String> SYS_DICTIONARY_CACHE = Maps.newHashMapWithExpectedSize(10);

    /**
     * 新增数据字典模块
     *
     * @param diskDictionary 需要新增的数据字典模块实体
     */
    @Override
    public void insert(DiskDictionary diskDictionary) {
        if (ObjectUtils.isEmpty(diskDictionary)) {
            return;
        }
        if (StringUtils.isBlank(diskDictionary.getBusinessId())) {
            diskDictionary.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DICTIONARY));
        }
        diskDictionaryDao.insert(diskDictionary);
    }

    /**
     * 批量新增数据字典模块
     *
     * @param list 需要新增的数据字典模块集合
     */
    @Override
    public void insertBatch(List<DiskDictionary> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(diskDictionary -> {
            List<DiskDictionary> diskDictionaryList = diskDictionary.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (DiskDictionary diskDictionaryInfo : diskDictionaryList) {
                if (StringUtils.isBlank(diskDictionaryInfo.getBusinessId())) {
                    diskDictionaryInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DICTIONARY));
                }
            }
            if (CollectionUtil.isNotEmpty(diskDictionaryList)) {
                diskDictionaryDao.insertBatch(diskDictionaryList);
            }
        });
    }

    /**
     * 删除数据字典模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return diskDictionaryDao.delete(businessId);
    }

    /**
     * 批量删除数据字典模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                diskDictionaryDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改数据字典模块
     *
     * @param diskDictionary 需要进行修改的数据字典模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(DiskDictionary diskDictionary) {
        if (ObjectUtils.isEmpty(diskDictionary) || StringUtils.isBlank(diskDictionary.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return diskDictionaryDao.update(diskDictionary);
    }

    /**
     * 批量修改数据字典模块
     *
     * @param list 需要进行修改的数据字典模块集合
     */
    @Override
    public void updateBatch(List<DiskDictionary> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(diskDictionary -> {
            List<DiskDictionary> diskDictionaryList = diskDictionary.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(diskDictionaryInfo -> StringUtils.isNotBlank(diskDictionaryInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diskDictionaryList)) {
                diskDictionaryDao.updateBatch(diskDictionaryList);
            }
        });
    }

    /**
     * 查找数据字典模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的数据字典模块实体
     */
    @Override
    public DiskDictionary getDiskDictionary(String businessId) {
        return diskDictionaryDao.getDiskDictionary(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的数据字典模块实体
     */
    @Override
    public DiskDictionary getDiskDictionaryToModel(String modelName, Object value) {
        return diskDictionaryDao.getDiskDictionaryToModel(modelName.replaceAll("([A-Z])", "_$1").toLowerCase(), value);
    }

    /**
     * 批量查找数据字典模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的数据字典模块数据集合
     */
    @Override
    public List<DiskDictionary> listDiskDictionary(List<String> list) {
        List<DiskDictionary> diskDictionaryList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> diskDictionaryList
                .addAll(diskDictionaryDao.listDiskDictionary(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return diskDictionaryList;
    }

    /**
     * 指定条件查找数据字典模块
     *
     * @param diskDictionary 需要查询的数据字典模块实体
     * @param startTime      需要查询的开始时间(如果有)
     * @param endTime        需要查询的结束时间(如果有)
     * @param page           页码
     * @param pageSize       每页大小
     * @return 数据字典模块搜索响应数据模型
     */
    @Override
    public PageResponse<DiskDictionary> listDiskDictionaryToKey(DiskDictionary diskDictionary, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<DiskDictionary> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean diskDictionaryIsBlank = diskDictionary == null || JSON.parseObject(JSONObject.toJSONString(diskDictionary)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!diskDictionaryIsBlank && timeIsBlank) {
            response.setRows(diskDictionaryDao.listDiskDictionaryToKey(diskDictionary, null, null, pageSql));
            response.setTotal(countSearch(diskDictionary, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (diskDictionaryIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(diskDictionaryDao.listDiskDictionaryToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 构建返回数据
        response.setRows(diskDictionaryDao.listDiskDictionaryToKey(diskDictionary, str[0], str[1], pageSql));
        response.setTotal(countSearch(diskDictionary, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找数据字典模块
     * 返回数据字典模块集合数据
     *
     * @param diskDictionary 需要查询的数据字典模块实体
     * @param startTime      需要查询的开始时间(如果有)
     * @param endTime        需要查询的结束时间(如果有)
     * @param page           页码
     * @param pageSize       每页大小
     * @return 返回数据字典模块集合
     */
    @Override
    public List<DiskDictionary> listDiskDictionary(DiskDictionary diskDictionary, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listDiskDictionaryToKey(diskDictionary, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有数据字典模块
     *
     * @return 返回所有的数据字典模块集合数据
     */
    @Override
    public List<DiskDictionary> listDiskDictionaryFindAll() {
        return diskDictionaryDao.listDiskDictionaryToKey(null, null, null, null);
    }

    /**
     * 获取当前jvm缓存中的字典数据，同时转换为指定的class类型
     *
     * @param dictionaryName 需要获取的字典名称
     * @param clazz          需要映射到的class类
     * @param <T>            需要映射到的类型
     * @return 从缓存中获取到的字典数据的内容
     */
    @Override
    public <T> T getDictionary(String dictionaryName, Class<T> clazz) {
        String dictionary = SYS_DICTIONARY_CACHE.get(dictionaryName);
        if (StringUtils.isBlank(dictionary)) {
            return null;
        }

        // 如果是String类型的，直接返回即可
        if (String.class.equals(clazz)) {
            return ConvertUtil.convert(dictionary);
        }

        return JSON.parseObject(dictionary, clazz);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param diskDictionary 需要查询的数据字典模块实体
     * @param startTime      需要查询的开始时间(如果有)
     * @param endTime        需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(DiskDictionary diskDictionary, String startTime, String endTime) {
        return diskDictionaryDao.countSearch(diskDictionary, startTime, endTime);
    }

    /**
     * Redis消息订阅者收到的消息
     *
     * @param message redis消息体
     * @param channel 当前消息体对应的通道
     */
    @Override
    public void receiveMessage(byte[] message, String channel) {
        if (ConstantConfig.Cache.ChanelEnum.CHANNEL_CONFIG.channel.equals(channel)) {
            // 获取当前所有系统缓存
            List<DiskDictionary> allDictionary = listDiskDictionaryFindAll();
            SYS_DICTIONARY_CACHE.clear();
            // 刷新当前缓存中的配置信息
            allDictionary.forEach(sequence -> SYS_DICTIONARY_CACHE.put(sequence.getDictionaryName(), sequence.getDictionaryContent()));
        }
    }

    /**
     * 初始化缓存内容
     */
    @Override
    public void run(String... args) {
        receiveMessage(null, ConstantConfig.Cache.ChanelEnum.CHANNEL_CONFIG.channel);
    }
}
