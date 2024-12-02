package com.bit.srb.core.controller.api;


import com.bit.common.result.R;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.pojo.vo.BorrowerVO;
import com.bit.srb.core.service.BorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/api/core/borrower")
@Slf4j
@Tag(name = "borrwer", description = "借款人管理")
public class BorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @Operation(description = "借款人信息提交")
    @PostMapping("/auth/save")
    public R postBorrwer(
            @Parameter(description = "借款人信息")
            @RequestBody BorrowerVO borrowerVO,
            HttpServletRequest httpServletRequest
            ){
        Long userId = JwtUtils.getUserId(httpServletRequest.getHeader("token"));

        borrowerService.saveBorrowerVOByUserId(borrowerVO, userId);
        return R.ok().message("信息提交成功");
    }

    @Operation(description = "借款人状态查询")
    @GetMapping("/auth/getBorrowerStatus")
    public R getBorrowerStatus(HttpServletRequest httpServletRequest){
        Long userId = JwtUtils.getUserId(httpServletRequest.getHeader("token"));
        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().data("borrowerStatus",status);
    }


}

