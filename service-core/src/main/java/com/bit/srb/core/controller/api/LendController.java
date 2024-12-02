package com.bit.srb.core.controller.api;


import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.Lend;
import com.bit.srb.core.service.LendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/api/core/lend")
@Slf4j
@Tag(name = "api-lend-controller", description = "投资界面")
public class LendController {

    @Resource
    private LendService lendService;

    @Operation(description = "获取标的列表")
    @GetMapping("/list")
    public R list()
    {
        List<Lend> lendList = lendService.selectList();
        return R.ok().data("lendList",lendList);
    }

    @Operation(description = "获取标的详情")
    @GetMapping("/show/{id}")
    public R show(
            @Parameter(description = "id")
            @PathVariable Long id
    ){
        Map<String, Object> lend = lendService.getLendById(id);
        return R.ok().data("lendDetail", lend);
    }

    @Operation(description = "计算投资收益")
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalmonth}/{returnMethod}")
    public R getInterestCount(
            @Parameter(description = "id")
            @PathVariable BigDecimal invest,
            @Parameter(description = "yearRate")
            @PathVariable BigDecimal yearRate,
            @Parameter(description = "totalmonth")
            @PathVariable Integer totalmonth,
            @Parameter(description = "returnMethod")
            @PathVariable Integer returnMethod){
        BigDecimal interestCount = lendService.getInterestCount(invest, yearRate, totalmonth, returnMethod);
        return R.ok().data("interestCount", interestCount);
    }

}

