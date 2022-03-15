package com.doudoudrive.userinfo.controller;

import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.util.http.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p></p>
 * <p>2022-03-04 13:10</p>
 *
 * @author Dan
 **/
@Validated
@RestController
@RequestMapping(value = "/disk-user")
public class DiskUserController {

    @ResponseBody
    @OpLog(title = "测试", businessType = "新增")
    @PostMapping(value = "/insert", produces = "application/json;charset=UTF-8")
    public Result<String> insert(@RequestBody DiskUser diskUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 新增用户模块
        return Result.ok("添加成功");
    }
}
