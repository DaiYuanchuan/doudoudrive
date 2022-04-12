package com.doudoudrive.auth.model.convert;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.auth.model.dto.UserInfoDTO;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.UserSimpleModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

/**
 * <p>用户模块信息实体数据转换器</p>
 * <p>2022-04-12 19:10</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Boolean.class, CharSequenceUtil.class, Date.class})
public interface UserInfoConvert {

    /**
     * 将UserInfoDTO(用户实体信息ES数据模型) 类型转换为 DiskUserModel(通用的用户信息数据模型)
     *
     * @param userInfoDto 用户实体信息ES数据模型
     * @return 通用的用户信息数据模型
     */
    DiskUserModel usernameSearchResponseConvert(UserInfoDTO userInfoDto);

    /**
     * 将UsernameSearchResponseDTO(通过用户名查询用户信息请求的响应数据模型) 类型转换为 UserSimpleModel(简单的用户信息数据模型)
     *
     * @param responseDTO 通过用户名查询用户信息请求的响应数据模型
     * @return 简单的用户信息数据模型
     */
    UserSimpleModel usernameSearchResponseConvertUserSimpleModel(UserInfoDTO responseDTO);

}
