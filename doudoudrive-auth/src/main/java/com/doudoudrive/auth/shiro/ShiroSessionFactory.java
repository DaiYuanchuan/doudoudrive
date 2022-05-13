package com.doudoudrive.auth.shiro;

import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

/**
 * <p>shiro的Session工厂类，实现原有SessionFactory</p>
 * <p>2022-04-17 20:38</p>
 *
 * @author Dan
 **/
public class ShiroSessionFactory implements SessionFactory {

    /**
     * 重写其中获取session方法
     *
     * @param initData 在{@link ShiroSession}创建期间使用的初始化数据。
     * @return 使用重写后的会话
     */
    @Override
    public ShiroSession createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                return new ShiroSession(host);
            }
        }
        return new ShiroSession();
    }
}
