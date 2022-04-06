package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.DiskUserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>用户登录模块响应数据模型</p>
 * <p>2022-04-04 21:45</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDTO {

    /**
     * 当前的用户信息数据模型
     */
    private DiskUserModel userInfo;

    /**
     * 登录的token，用于后续鉴权
     */
    private String token;

    /**
     * 账号被封禁时间的格式化显示(最大显示粒度为天)
     */
    private String userBanTimeFormat;

}
