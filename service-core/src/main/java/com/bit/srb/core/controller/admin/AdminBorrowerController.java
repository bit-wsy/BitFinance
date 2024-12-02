package com.bit.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.Borrower;
import com.bit.srb.core.pojo.vo.BorrowerApprovalVO;
import com.bit.srb.core.pojo.vo.BorrowerDetailVO;
import com.bit.srb.core.service.BorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/core/borrower")
@Tag(name = "admin-borrower-controller", description = "借款人信息管理")
public class AdminBorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @Operation(description = "分页显示借款人信息")
    @GetMapping("/list/{page}/{limit}")
    public R listByPage(
            @Parameter(description = "当前页数")
            @PathVariable Long page,
            @Parameter(description = "每页条数")
            @PathVariable Long limit,
            @Parameter(description = "搜索关键词")
            @RequestParam  String keyword){

        // !!!!!!!!!!
        Page<Borrower> borrowerPage = new Page<>(page, limit);
        IPage<Borrower> pageModel = borrowerService.listBorrowerBypage(borrowerPage, keyword);

        return R.ok().data("pageModel", pageModel);
    }

    @Operation(description = "借款人详情界面")
    @GetMapping("/show/{id}")
    public R getBorrowerDetailById(
            @Parameter(description = "用户id")
            @PathVariable Long id
    ){
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.ok().data("borrowerDetailVO", borrowerDetailVO);
    }

    @Operation
    @PostMapping("/approval")
    public R borrowerApproval(
            @Parameter(description = "审核信息")
            @RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.getApproval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }
}
