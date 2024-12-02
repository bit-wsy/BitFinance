package com.bit.srb.core.controller.admin;


import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.BorrowInfo;
import com.bit.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.bit.srb.core.service.BorrowInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/core/borrowInfo")
@Slf4j
@Tag(name = "admin-borrow-info-controller",description = "借款申请")
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @Operation(description = "获取借款申请列表")
    @GetMapping("/list")
    public R getList(){
        List<BorrowInfo> borrowInfo = borrowInfoService.getBorrowInfoList();
        return R.ok().data("list", borrowInfo);
    }

    @Operation(description = "获取借款申请详情")
    @GetMapping("/show/{id}")
    public R getDetailByUserId(
            @Parameter(description = "借款人id")
            @PathVariable Long id
    ){
        Map<String, Object> borrowInfoDetail =  borrowInfoService.getBorrowInfoByUserId(id);
        return R.ok().data("borrowInfoDetail", borrowInfoDetail);
    }

    @Operation(description = "借款信息审核")
    @PostMapping("/approval")
    public R borrowInfoApproval(
            @Parameter(description = "借款审核表单")
            @RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO
            ){
        borrowInfoService.borrowInfoApproval(borrowInfoApprovalVO);
        return R.ok().message("借款审核完成");
    }
}
