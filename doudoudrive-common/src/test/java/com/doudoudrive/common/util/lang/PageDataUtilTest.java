package com.doudoudrive.common.util.lang;

import com.doudoudrive.common.model.dto.model.PageBean;
import com.doudoudrive.common.model.dto.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * <p>构建分页的sql工具测试类</p>
 * <p>2022-05-30 19:23</p>
 *
 * @author Dan
 **/
@Slf4j
public class PageDataUtilTest {

    @Test
    public void pangingSql() {
        String limit = " limit 10,10 ";
        PageBean pageBean = PageBean.builder()
                .page(2)
                .pageSize(10)
                .build();
        Assert.isTrue(limit.equals(PageDataUtil.pangingSql(pageBean)), "分页sql构建失败");
        Assert.isTrue(limit.equals(PageDataUtil.pangingSql(2)), "分页sql构建失败");
        PageResponse<?> pageResponse = new PageResponse<>();
        Assert.isTrue(limit.equals(PageDataUtil.pangingSql(2, 10, pageResponse)), "分页sql构建失败");
        Assert.isTrue(pageBean.getPage().equals(pageResponse.getPage())
                && pageBean.getPageSize().equals(pageResponse.getPageSize()), "分页sql构建失败");
    }
}
