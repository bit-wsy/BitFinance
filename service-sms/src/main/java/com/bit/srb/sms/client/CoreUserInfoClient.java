package com.bit.srb.sms.client;

import com.bit.srb.sms.client.fallback.CoreUserInfoClientFallBack;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallBack.class)
public interface CoreUserInfoClient {

    @Operation(description = "校验手机号是否被注册")
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}") // 这里的定义必须完整
    boolean checkMobile(
            @Parameter(description = "手机号")
            @PathVariable String mobile);
}
