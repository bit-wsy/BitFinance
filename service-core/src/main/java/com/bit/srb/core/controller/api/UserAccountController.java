package com.bit.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.bit.common.result.R;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/api/core/userAccount")
@Slf4j
@Tag(name = "api-core-userAccount")
public class UserAccountController {

    @Resource
    private UserAccountService userAccountService;

    @Operation(description = "用户充值")
    @PostMapping("auth/commitCharge/{chargeAmt}")
    public R commitCharge(
            @PathVariable BigDecimal chargeAmt,
            @RequestHeader("X-User-Id") Long userId){
        String formStr = userAccountService.commitCharge(userId, chargeAmt);
        return R.ok().data("formStr",formStr);
    }

    @Operation(description = "回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest httpServletRequest){
        // 需要失败重试 返回fail 否则返回success
        Map<String, Object> paramMap = RequestHelper.switchMap(httpServletRequest.getParameterMap());
        log.info("用户充值异步回调：" + JSON.toJSONString(paramMap));
        // 校验签名
        if(RequestHelper.isSignEquals(paramMap)){
            if(paramMap.get("resultCode").equals("0001")){ // 充值成功
                return userAccountService.nofity(paramMap);
            }else{
                log.info("用户充值异步回调充值失败：" + JSON.toJSONString(paramMap));
                return "success";
            }

        }else{
            log.info("用户充值异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
    }

    @Operation(description = "获取账户余额信息")
    @GetMapping("auth/getAccount")
    public R getAccount(
            @RequestHeader("X-User-Id") Long userId){
        BigDecimal amount = userAccountService.getAmount(userId);
        return R.ok().data("account", amount);
    }

    @Operation(description = "用户提现")
    @PostMapping("auth/commitWithdraw/{fetchAmt}")
    public R commitWithdraw(
            @PathVariable BigDecimal fetchAmt,
            @RequestHeader("X-User-Id") Long userId){
        String formStr = userAccountService.commitWithdraw(userId, fetchAmt);
        return R.ok().data("formStr", formStr);
    }

    @Operation(description = "提现回调")
    @PostMapping("/notifyWithdraw")
    public String notifyWithdraw(HttpServletRequest httpServletRequest){
        Map<String, Object> paramMap = RequestHelper.switchMap(httpServletRequest.getParameterMap());
        log.info("用户提现异步回调：" + JSON.toJSONString(paramMap));
        // 验签
        if(RequestHelper.isSignEquals(paramMap)){
            if(paramMap.get("resultCode").equals("0000")){
                userAccountService.notifyWithdraw(paramMap);
            }else{
                log.info("用户提现异步回调失败：" + JSON.toJSONString(paramMap));
                return "success";
            }
        }else{
            log.warn("用户提现异步回调验签失败：" + JSON.toJSONString(paramMap));
            return "fail";
        }

        return "success";
    }
}

