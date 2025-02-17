package com.bit.srb.core.controller.api;


import com.bit.common.result.R;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.pojo.entity.LendReturn;
import com.bit.srb.core.service.LendReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Slf4j
@RestController
@RequestMapping("api/core/lendReturn")
public class LendReturnController {
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

    @Operation(description = "还款")
    @PostMapping("/auth/commitReturn/{lendReturnId}")
    public R commitReturn(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "还款计划id")
            @PathVariable Long lendReturnId){
        String formStr = lendReturnService.commitReturn(lendReturnId,userId);
        return R.ok().data("formStr", formStr);
    }

    @Operation(description = "还款回调")
    @PostMapping("notifyUrl")
    public String notifyUrl(HttpServletRequest httpServletRequest){
        // 参数map转换
        Map<String, Object> paramMap = RequestHelper.switchMap(httpServletRequest.getParameterMap());
        // 验证签名
        if(RequestHelper.isSignEquals(paramMap)){
            if(paramMap.get("resultCode").equals("0001")){
                lendReturnService.notifyUrl(paramMap);
            }else{
                log.info("还款回调失败");
                return "fail";
            }
        }else{
            log.info("还款回调验签失败");
            return "fail";
        }
        return "success";
    }
}

