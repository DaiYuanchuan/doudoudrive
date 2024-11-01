package com.doudoudrive.commonservice.service;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.DiskDictionary;

import java.util.List;

/**
 * <p>数据字典模块服务层接口</p>
 * <p>2022-04-07 20:10:02</p>
 *
 * @author Dan
 */
public interface DiskDictionaryService {

    /**
     * 新增数据字典模块
     *
     * @param diskDictionary 需要新增的数据字典模块实体
     */
    void insert(DiskDictionary diskDictionary);

    /**
     * 批量新增数据字典模块
     *
     * @param list 需要新增的数据字典模块集合
     */
    void insertBatch(List<DiskDictionary> list);

    /**
     * 删除数据字典模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除数据字典模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改数据字典模块
     *
     * @param diskDictionary 需要进行修改的数据字典模块实体
     * @return 返回修改的条数
     */
    Integer update(DiskDictionary diskDictionary);

    /**
     * 批量修改数据字典模块
     *
     * @param list 需要进行修改的数据字典模块集合
     */
    void updateBatch(List<DiskDictionary> list);

    /**
     * 查找数据字典模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的数据字典模块实体
     */
    DiskDictionary getDiskDictionary(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的数据字典模块实体
     */
    DiskDictionary getDiskDictionaryToModel(String modelName, Object value);

    /**
     * 批量查找数据字典模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的数据字典模块数据集合
     */
    List<DiskDictionary> listDiskDictionary(List<String> list);

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
    PageResponse<DiskDictionary> listDiskDictionaryToKey(DiskDictionary diskDictionary, String startTime, String endTime, Integer page, Integer pageSize);

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
    List<DiskDictionary> listDiskDictionary(DiskDictionary diskDictionary, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 查找所有数据字典模块
     *
     * @return 返回所有的数据字典模块集合数据
     */
    List<DiskDictionary> listDiskDictionaryFindAll();

    /**
     * 获取当前jvm缓存中的字典数据，同时转换为指定的class类型
     *
     * @param dictionaryName 需要获取的字典名称
     * @param clazz          需要映射到的class类
     * @param <T>            需要映射到的类型
     * @return 从缓存中获取到的字典数据的内容
     */
    <T> T getDictionary(String dictionaryName, Class<T> clazz);

    /**
     * 获取当前jvm缓存中的字典数据，同时转换为指定的List类型
     *
     * @param dictionaryName 需要获取的字典名称
     * @param clazz          需要映射到的class类
     * @param <T>            需要映射到的类型
     * @return 从缓存中获取到的字典数据的内容
     */
    <T> List<T> getDictionaryArray(String dictionaryName, Class<T> clazz);

    /**
     * 返回搜索结果的总数
     *
     * @param diskDictionary 需要查询的数据字典模块实体
     * @param startTime      需要查询的开始时间(如果有)
     * @param endTime        需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(DiskDictionary diskDictionary, String startTime, String endTime);

    /**
     * 鉴权参数加密
     *
     * @param object 需要鉴权的参数对象
     * @return 加密后的签名
     */
    String encrypt(Object object);

    /**
     * 鉴权参数解密
     *
     * @param sign  签名
     * @param clazz 签名解密后需要转换的对象类
     * @return 解密后的对象串，如果解密失败则返回null
     */
    <T> T decrypt(String sign, Class<T> clazz);

    /**
     * 获取对称加密SymmetricCrypto对象
     *
     * @return SymmetricCrypto对象
     */
    SymmetricCrypto getSymmetricCrypto();
}