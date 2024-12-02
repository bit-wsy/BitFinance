package com.bit.srb.core.controller.api;


import com.bit.common.result.R;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.pojo.entity.TransFlow;
import com.bit.srb.core.service.TransFlowService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 交易流水表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Slf4j
@RestController
@RequestMapping("api/core/transFlow")
public class TransFlowController {

    @Resource
    private TransFlowService transFlowService;

    @Operation(description = "获取个人资金列表")
    @GetMapping("/list")
    public R getTransFlowList(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<TransFlow> transFlowList = transFlowService.getTransFlowList(userId);
        return R.ok().data("list", transFlowList);
    }
}

