package com.doudoudrive.common.util.lang;

import com.doudoudrive.common.model.pojo.DiskUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * <p>通过Java实体类的get方法名获取对应的属性名测试类</p>
 * <p>2022-04-05 17:46</p>
 *
 * @author Dan
 **/
@Slf4j
public class ReflectUtilTest {

    @Test
    public void propertyTest() {
        String attribute = ReflectUtil.property(DiskUser::getUserName);
        log.info("DiskUser::getBusinessId : {}", attribute);
    }

    @Test
    public void propertyToUnderlineTest() {
        String attribute = ReflectUtil.propertyToUnderline(DiskUser::getBusinessId);
        log.info("DiskUser::getBusinessId : {}", attribute);
    }
}
