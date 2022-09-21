package com.doudoudrive.common.config;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.util.http.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>页面视图配置</p>
 * <p>2022-03-21 15:18</p>
 *
 * @author Dan
 **/
@Slf4j
@RestController
public class ViewConfig {

    @SneakyThrows
    @RequestMapping(value = "/404", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> jumpNotFound(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);
        return Result.build(StatusCodeEnum.NOT_FOUND);
    }
}
