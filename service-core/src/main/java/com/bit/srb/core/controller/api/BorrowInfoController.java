package com.bit.srb.core.controller.api;


import com.bit.common.result.R;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.pojo.entity.BorrowInfo;
import com.bit.srb.core.service.BorrowInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/api/core/borrowInfo")
@Slf4j
@Tag(name = "borrower-info-controller", description = "借款信息")
public class BorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @Operation(description = "获取用户借款信息")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrow(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmountByUserId(userId);
        return R.ok().data("borrowAmount",borrowAmount);
    }

    @Operation
    @PostMapping("/auth/save")
    public R save(
            @Parameter(description = "提交借款申请")
            @RequestBody BorrowInfo borrowInfo,
            HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowInfoService.saveBorrowInfoById(userId, borrowInfo);
        return R.ok().message("借款申请提交成功");
    }

    @Operation(description = "借款状态查询")
    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowInfoStatus(HttpServletRequest httpServletRequest){
        Long userId = JwtUtils.getUserId(httpServletRequest.getHeader("token"));
        Integer status = borrowInfoService.getStatusByUserId(userId);
        return R.ok().data("borrowInfoStatus",status);
    }
}

