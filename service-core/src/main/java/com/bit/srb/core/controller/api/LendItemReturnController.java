package com.bit.srb.core.controller.api;


import com.bit.common.result.R;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.pojo.entity.LendItemReturn;
import com.bit.srb.core.service.LendItemReturnService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@Slf4j
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController {
    @Resource
    private LendItemReturnService lendItemReturnService;

    @Operation(description = "投资人获取还款回款列表")
    @GetMapping("/auth/list/{lendId}")
    public R getLendItemReturnByUserIdAndLendId(
            @PathVariable Long lendId,
            HttpServletRequest httpServletRequest
    ){
        String token = httpServletRequest.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<LendItemReturn> lendItemReturnList =  lendItemReturnService.getLendItemReturnByUserIdAndLendId(userId, lendId);
        return R.ok().data("list", lendItemReturnList);
    }
}

