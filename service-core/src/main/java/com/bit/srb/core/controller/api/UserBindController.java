package com.bit.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.bit.common.result.R;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.pojo.vo.UserBindVO;
import com.bit.srb.core.service.UserBindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
@Tag( name = "user-bind-controller", description = "会员账号绑定")
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @Operation(description = "提交账号绑定")
    @PostMapping("/auth/bind")
    public R bind(
            @RequestBody UserBindVO userBindVO,
            @RequestHeader("X-User-Id") Long userId
    ){
        // 用户绑定，根据id返回动态表单的字符串
        String formStr = userBindService.commitBindUser(userBindVO, userId);
        return R.ok().data("formStr", formStr);
    }

    @Operation(description = "账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        // 获取汇付宝返回的参数
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());// ParameterMap返回所有请求参数的映射
        log.info("从汇付宝返回的参数" + JSON.toJSONString(paramMap));
        // 校验签名
        if(!RequestHelper.isSignEquals(paramMap)){
            log.error("用户绑定签名校验失败");
            return "fail";
        }
        userBindService.notify(paramMap);
        log.info("用户绑定成功");
        return "success";
    }
}

