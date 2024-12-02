package com.bit.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.bit.common.result.R;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.pojo.entity.LendItem;
import com.bit.srb.core.pojo.vo.InvestVO;
import com.bit.srb.core.service.LendItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@Slf4j
@Tag(name = "api-lenditem-controller", description = "投标")
@RequestMapping("/api/core/lendItem")
public class LendItemController {

    @Resource
    private LendItemService lendItemService;

    @Operation(description = "投标")
    @PostMapping("/auth/commitInvest")
    public R commitInvest(
            @Parameter(description = "投资人信息")
            @RequestBody InvestVO investVO,
            HttpServletRequest httpServletRequest
            ){
        String token = httpServletRequest.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);

        investVO.setInvestUserId(userId);
        investVO.setInvestName(userName);

        String formStr = lendItemService.commitInvest(investVO);
        return R.ok().data("formStr", formStr);
    }

    @Operation(description = "回调函数")
    @PostMapping("/notify")
    public String notify(
            HttpServletRequest httpServletRequest){
        Map<String, Object> paramMap = RequestHelper.switchMap(httpServletRequest.getParameterMap());
        log.info("用户投资异步回调：" + JSON.toJSONString(paramMap));
        // 验证签名
        if(RequestHelper.isSignEquals(paramMap)){
            if(paramMap.get("resultCode").equals("0001")){
                lendItemService.notify(paramMap);
                return "success";
            }else{
                log.info("用户投资异步回调充值失败：" + JSON.toJSONString(paramMap));
                return "success";
            }
        }else{
            log.info("用户投资异步回调签名错误");
            return "fail";
        }
    }

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

