package com.bit.srb.core.controller.admin;

import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.LendReturn;
import com.bit.srb.core.service.LendReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("admin/core/lendReturn")
public class AdminLendReturnController {

    @Resource
    private LendReturnService lendReturnService;

    @Operation(description = "获取还款计划列表")
    @GetMapping("/list/{lendId}")
    public R getLendReturnListByLendId(
            @Parameter(description = "标的id")
            @PathVariable Long lendId
    ){
        List<LendReturn> lendReturnList = lendReturnService.getLendReturnListByLendId(lendId);
        return R.ok().data("list", lendReturnList);
    }
}
