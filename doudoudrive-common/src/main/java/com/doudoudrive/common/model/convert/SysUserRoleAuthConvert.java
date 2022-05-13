package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.model.dto.model.SysUserAuthModel;
import com.doudoudrive.common.model.pojo.SysRoleAuth;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * <p>系统用户角色、权限信息等相关的数据实体类型转换器</p>
 * <p>2022-04-06 17:20</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysUserRoleAuthConvert {

    /**
     * 将SysRoleAuth(角色、权限关联模块数据模型) 类型批量转换为 SysUserAuthModel(系统用户权限数据模型)
     *
     * @param sysRoleAuthList 角色、权限关联模块数据模型
     * @return 系统用户权限数据模型
     */
    List<SysUserAuthModel> sysRoleAuthListConvert(List<SysRoleAuth> sysRoleAuthList);

}
