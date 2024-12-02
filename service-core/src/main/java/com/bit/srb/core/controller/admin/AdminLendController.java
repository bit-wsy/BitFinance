package com.bit.srb.core.controller.admin;


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
@RequestMapping("/admin/core/lend")
@Slf4j
@Tag(name = "admin-lend-controller", description = "标的管理")
public class AdminLendController {

    @Resource
    private LendService lendService;



    @Operation(description = "获取标的列表")
    @GetMapping("/list")
    public R list()
    {
        List<Lend> lendList = lendService.selectList();
        return R.ok().data("list",lendList);
    }

    @Operation(description = "获取标的详情")
    @GetMapping("/show/{id}")
    public R show(
            @Parameter(description = "id")
            @PathVariable Long id
    ){
        Map<String, Object> lendDetail = lendService.getLendById(id);
        return R.ok().data("lendDetail", lendDetail);
    }

    @Operation(description = "放款")
    @GetMapping("/makeLoan/{id}")
    public R makeLoan(
            @Parameter(description = "标的id")
            @PathVariable Long id){
        lendService.makeLoan(id);
        return R.ok().message("放款成功");
    }

}

