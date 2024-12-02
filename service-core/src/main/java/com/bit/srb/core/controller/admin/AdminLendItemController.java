package com.bit.srb.core.controller.admin;

import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.LendItem;
import com.bit.srb.core.service.LendItemService;
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

@RestController
@RequestMapping("/admin/core/lendItem")
@Slf4j
@Tag(name = "admin-lenditem-controller", description = "投资管理")
public class AdminLendItemController {
    @Resource
    private LendItemService lendItemService;

    @Operation(description = "获取投资记录")
    @GetMapping("/list/{lendId}")
    public R getLendItemList(
            @Parameter(description = "标的id")
            @PathVariable Long lendId
    ){
        List<LendItem> lendItemList = lendItemService.getAllLendItemByLendId(lendId);
        return R.ok().data("list", lendItemList);
    }
}
