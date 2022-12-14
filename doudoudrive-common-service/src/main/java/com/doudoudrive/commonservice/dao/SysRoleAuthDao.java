package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.SysRoleAuth;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>角色、权限关联模块数据访问层</p>
 * <p>2022-04-06 13:57</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.USERINFO)
public interface SysRoleAuthDao {

    /**
     * 新增角色、权限关联模块
     *
     * @param sysRoleAuth 需要新增的角色、权限关联模块实体
     * @return 返回新增的条数
     */
    Integer insert(@Param("sysRoleAuth") SysRoleAuth sysRoleAuth);

    /**
     * 批量新增角色、权限关联模块
     *
     * @param list 需要新增的角色、权限关联模块集合
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<SysRoleAuth> list);

    /**
     * 删除角色、权限关联模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId);

    /**
     * 批量删除角色、权限关联模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("list") List<String> list);

    /**
     * 修改角色、权限关联模块
     *
     * @param sysRoleAuth 需要进行修改的角色、权限关联模块实体
     * @return 返回修改的条数
     */
    Integer update(@Param("sysRoleAuth") SysRoleAuth sysRoleAuth);

    /**
     * 批量修改角色、权限关联模块
     *
     * @param list 需要进行修改的角色、权限关联模块集合
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<SysRoleAuth> list);

    /**
     * 查找角色、权限关联模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的角色、权限关联模块实体
     */
    SysRoleAuth getSysRoleAuth(@Param("businessId") String businessId);

    /**
     * 根据数据表中某个成员变量名称(非实体类中property的名称)查找(value需符合unique约束)
     *
     * @param modelName 数据表中某个成员变量名称,非实体类中property的名称[如:create_time]
     * @param value     需要查找的值
     * @return 返回查找到的角色、权限关联模块实体
     */
    SysRoleAuth getSysRoleAuthToModel(@Param("modelName") String modelName, @Param("value") Object value);

    /**
     * 批量查找角色、权限关联模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的角色、权限关联模块数据集合
     */
    List<SysRoleAuth> listSysRoleAuth(@Param("list") List<String> list);

    /**
     * 指定条件查找角色、权限关联模块
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @param limit       分页的SQL语句
     * @return 返回查找到的角色、权限关联模块数据集合
     */
    List<SysRoleAuth> listSysRoleAuthToKey(@Param("sysRoleAuth") SysRoleAuth sysRoleAuth, @Param("startTime") String startTime,
                                           @Param("endTime") String endTime, @Param("limit") String limit);

    /**
     * 返回搜索结果的总数
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(@Param("sysRoleAuth") SysRoleAuth sysRoleAuth, @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

    // ====================================================== 截断 =====================================================

    /**
     * 根据角色编码批量查询当前角色下绑定的所有权限编码
     *
     * @param roleCodeList 角色编码列表
     * @return 返回指定角色下所有的权限编码
     */
    List<SysRoleAuth> listSysRoleAuthToRoleCode(@Param("roleCodeList") List<String> roleCodeList);

    /**
     * 根据权限编码查询所有拥有此权限的角色数据
     *
     * @param authCode 权限编码
     * @return 返回所有拥有指定权限的角色数据
     */
    List<SysRoleAuth> listSysRoleAuthToAuthCode(@Param("authCode") String authCode);
}
