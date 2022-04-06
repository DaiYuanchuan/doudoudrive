package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.SysRoleAuth;

import java.util.List;

/**
 * <p>角色、权限关联模块服务层接口</p>
 * <p>2022-04-06 15:15</p>
 *
 * @author Dan
 **/
public interface SysRoleAuthService {

    /**
     * 新增角色、权限关联模块
     *
     * @param sysRoleAuth 需要新增的角色、权限关联模块实体
     */
    void insert(SysRoleAuth sysRoleAuth);

    /**
     * 批量新增角色、权限关联模块
     *
     * @param list 需要新增的角色、权限关联模块集合
     */
    void insertBatch(List<SysRoleAuth> list);

    /**
     * 删除角色、权限关联模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除角色、权限关联模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改角色、权限关联模块
     *
     * @param sysRoleAuth 需要进行修改的角色、权限关联模块实体
     * @return 返回修改的条数
     */
    Integer update(SysRoleAuth sysRoleAuth);

    /**
     * 批量修改角色、权限关联模块
     *
     * @param list 需要进行修改的角色、权限关联模块集合
     */
    void updateBatch(List<SysRoleAuth> list);

    /**
     * 查找角色、权限关联模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的角色、权限关联模块实体
     */
    SysRoleAuth getSysRoleAuth(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的角色、权限关联模块实体
     */
    SysRoleAuth getSysRoleAuthToModel(String modelName, Object value);

    /**
     * 批量查找角色、权限关联模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的角色、权限关联模块数据集合
     */
    List<SysRoleAuth> listSysRoleAuth(List<String> list);

    /**
     * 指定条件查找角色、权限关联模块
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @param page        页码
     * @param pageSize    每页大小
     * @return 角色、权限关联模块搜索响应数据模型
     */
    PageResponse<SysRoleAuth> listSysRoleAuthToKey(SysRoleAuth sysRoleAuth, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 指定条件查找角色、权限关联模块
     * 返回角色、权限关联模块集合数据
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @param page        页码
     * @param pageSize    每页大小
     * @return 返回角色、权限关联模块集合
     */
    List<SysRoleAuth> listSysRoleAuth(SysRoleAuth sysRoleAuth, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 查找所有角色、权限关联模块
     *
     * @return 返回所有的角色、权限关联模块集合数据
     */
    List<SysRoleAuth> listSysRoleAuthFindAll();

    /**
     * 返回搜索结果的总数
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(SysRoleAuth sysRoleAuth, String startTime, String endTime);

    // ====================================================== 截断 =====================================================

    /**
     * 根据角色编码批量查询当前角色下绑定的所有权限编码
     *
     * @param roleCodeList 角色编码列表
     * @return 返回指定角色下所有的权限编码
     */
    List<SysRoleAuth> listSysRoleAuthToRoleCode(List<String> roleCodeList);

    /**
     * 根据权限编码查询所有拥有此权限的角色数据
     *
     * @param authCode 权限编码
     * @return 返回所有拥有指定权限的角色数据
     */
    List<SysRoleAuth> listSysRoleAuthToAuthCode(String authCode);

}
