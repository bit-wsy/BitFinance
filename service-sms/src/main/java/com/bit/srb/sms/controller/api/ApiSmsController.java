package com.bit.srb.sms.controller.api;

import com.bit.common.exception.Assert;
import com.bit.common.result.R;
import com.bit.common.result.ResponseEnum;
import com.bit.common.util.RandomUtils;
import com.bit.common.util.RegexValidateUtils;
import com.bit.srb.sms.client.CoreUserInfoClient;
import com.bit.srb.sms.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("/api/sms")
@Tag(name = "api-sms-controller", description = "短信管理")
public class ApiSmsController {

    @Resource
    private SmsService smsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @Operation(description = "获取验证码")
    @GetMapping("/send/{mobile}")
    public R send(
            @Parameter(description = "手机号", required = true)
            @PathVariable String mobile){
        // 手机号检查
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        // 远程调用 ：是否已经被注册
        boolean result = coreUserInfoClient.checkMobile(mobile);
        Assert.isTrue(!result, ResponseEnum.MOBILE_EXIST_ERROR);

        // 验证吗
        HashMap<String, Object> map = new HashMap<>();

        String code = RandomUtils.getFourBitRandom();
        map.put("code", code);//使用随机数工具

//        smsService.send(mobile, SmsProperties.TEMPLATE_CODE, map);

        // 验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, code, 5, TimeUnit.MINUTES);
        return R.ok().message("短信发送成功");
    }
}
