package com.doudoudrive.common.model.convert;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.SecretSaltingInfo;
import com.doudoudrive.common.model.dto.model.UserConfidentialInfo;
import com.doudoudrive.common.model.dto.model.UserSimpleModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.response.UserinfoSearchResponseDTO;
import com.doudoudrive.common.model.pojo.DiskUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * <p>用户模块信息等相关的实体信息转换器</p>
 * <p>2022-03-22 23:36</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Boolean.class, CharSequenceUtil.class, Date.class, DigestUtils.class})
public interface DiskUserInfoConvert {

    /**
     * 将SaveUserInfoRequestDTO(保存用户信息时的请求数据模型) 类型转换为 DiskUser(用户模块实体类)
     * 转换时为 DiskUser(用户模块实体类) 的用户密码、盐值重新赋值
     *
     * @param requestDTO  保存用户信息时的请求数据模型
     * @param saltingInfo 密码加盐处理结果
     * @param avatar      用户头像
     * @return 用户模块实体类
     */
    @Mappings({
            @Mapping(target = "userAvatar", source = "avatar"),
            @Mapping(target = "userPwd", source = "saltingInfo.password"),
            @Mapping(target = "userSalt", source = "saltingInfo.salt"),
            @Mapping(target = "secretKey", expression = "java(DigestUtils.md5DigestAsHex((requestDTO.getUserName() + \"#\" + requestDTO.getUserPwd()).getBytes()))"),
            @Mapping(target = "available", expression = "java(Boolean.TRUE)"),
            @Mapping(target = "userReason", expression = "java(CharSequenceUtil.EMPTY)"),
            @Mapping(target = "userBanTime", constant = NumberConstant.STRING_ZERO),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    DiskUser saveUserInfoRequestConvert(SaveUserInfoRequestDTO requestDTO, SecretSaltingInfo saltingInfo, String avatar);

    /**
     * 将DiskUser(用户模块实体类) 类型转换为 SaveElasticsearchUserInfoRequestDTO(保存es用户信息时的请求数据模型)
     *
     * @param diskUser    用户模块实体类
     * @param tableSuffix 表后缀
     * @return 保存es用户信息时的请求数据模型
     */
    SaveElasticsearchUserInfoRequestDTO diskUserInfoConvert(DiskUser diskUser, String tableSuffix);

    /**
     * 将UsernameSearchResponseDTO(用户信息查询时请求的响应数据模型) 类型转换为 DiskUserModel(通用的用户信息数据模型)
     *
     * @param responseDTO 用户信息查询时请求的响应数据模型
     * @return 通用的用户信息数据模型
     */
    DiskUserModel usernameSearchResponseConvert(UserinfoSearchResponseDTO responseDTO);

    /**
     * 将UsernameSearchResponseDTO(用户信息查询时请求的响应数据模型) 类型转换为 UserConfidentialInfo(用户机密数据信息模型)
     *
     * @param responseDTO 用户信息查询时请求的响应数据模型
     * @return 用户机密数据信息模型
     */
    UserConfidentialInfo usernameSearchResponseConvertUserConfidential(UserinfoSearchResponseDTO responseDTO);

    /**
     * 将DiskUser(用户模块实体类) 类型转换为 UpdateElasticsearchUserInfoRequestDTO(更新es用户信息时的请求数据模型)
     *
     * @param userinfo 用户信息查询时请求的响应数据模型
     * @return 用户模块实体类
     */
    UpdateElasticsearchUserInfoRequestDTO diskUserConvert(DiskUser userinfo);

    /**
     * 将UsernameSearchResponseDTO(用户信息查询时请求的响应数据模型) 类型转换为 UserSimpleModel(简单的用户信息数据模型)
     *
     * @param responseDTO 用户信息查询时请求的响应数据模型
     * @return 简单的用户信息数据模型
     */
    UserSimpleModel usernameSearchResponseConvertUserSimpleModel(UserinfoSearchResponseDTO responseDTO);

    /**
     * 将UserSimpleModel(简单的用户信息数据模型) 类型转换为 DiskUserModel(通用的用户信息数据模型)
     *
     * @param userSimpleModel 简单的用户信息数据模型
     * @return 通用的用户信息数据模型
     */
    DiskUserModel userSimpleModelConvert(UserSimpleModel userSimpleModel);

    /**
     * 将DiskUser(用户模块实体类) 类型转换为 DiskUserModel(通用的用户信息数据模型)
     *
     * @param diskUser 用户模块实体类
     * @return 通用的用户信息数据模型
     */
    DiskUserModel diskUserConvertUserModel(DiskUser diskUser);

}
