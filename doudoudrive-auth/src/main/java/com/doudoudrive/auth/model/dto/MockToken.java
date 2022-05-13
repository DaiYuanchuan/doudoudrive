package com.doudoudrive.auth.model.dto;

import com.doudoudrive.common.model.dto.model.LoginType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>免密登陆用户的Token类型</p>
 * <p>仿照UsernamePasswordToken, 创建的一个属于免密登陆用户的Token类型, 叫MockToke</p>
 * <p>2020-04-23 23:16</p>
 *
 * @author Dan
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MockToken extends UsernamePasswordToken implements Serializable {

    @Serial
    private static final long serialVersionUID = -2564928913725078138L;

    private LoginType type = LoginType.PASSWORD;

    /**
     * 免密登录
     */
    public MockToken(String username) {
        super(username, "", false, null);
        this.type = LoginType.NO_PASSWORD;
    }

    /**
     * 账号密码登录
     */
    public MockToken(String username, String password) {
        super(username, password, false, null);
    }
}
