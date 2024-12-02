package com.bit.srb.core.admin;


import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.UserLoginRecord;
import com.bit.srb.core.service.UserLoginRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/admin/core/userLoginRecord")
//@CrossOrigin
@Slf4j
@Tag(name = "admin-user-loginrecord-controller", description = "会员登录日志接口")
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @Operation(description = "获取用户登录日志")
    @GetMapping("/listTop50/{userId}")
    public R listTop50(
            @Parameter(description = "用户id")
            @PathVariable Long userId
    ){
        List<UserLoginRecord> userLoginRecordList = userLoginRecordService.listTop50(userId);
        return R.ok().data("list", userLoginRecordList);
    }
}

