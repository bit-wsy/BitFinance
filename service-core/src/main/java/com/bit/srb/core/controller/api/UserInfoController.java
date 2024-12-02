package com.bit.srb.core.controller.api;


import com.bit.common.exception.Assert;
import com.bit.common.result.R;
import com.bit.common.result.ResponseEnum;
import com.bit.common.util.RegexValidateUtils;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.pojo.vo.LogInVO;
import com.bit.srb.core.pojo.vo.RegisterVO;
import com.bit.srb.core.pojo.vo.UserIndexVO;
import com.bit.srb.core.pojo.vo.UserInfoVO;
import com.bit.srb.core.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/api/core/userInfo")
@Tag( name = "user-info-controller", description = "会员接口")
//@CrossOrigin
@Slf4j
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    @Operation(description = "会员注册")
    @PostMapping("/register")
    public R register(
            //@Parameter("")
            @RequestBody RegisterVO registerVO){

        String code = registerVO.getCode();
        String mobile = registerVO.getMobile();
        String password = registerVO.getPassword();

        // 表单校验
        Assert.notEmpty(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(code,ResponseEnum.CODE_NULL_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        String codeGen = (String)redisTemplate.opsForValue().get("srb:sms:code:" + mobile);

        // 验证码错误
        Assert.equals(code, codeGen, ResponseEnum.CODE_ERROR);

        userInfoService.register(registerVO);

        return R.ok().message("注册成功");
    }

    @Operation(description = "会员登录")
    @PostMapping("/login")
    public R logIn(@RequestBody LogInVO logInVO,
                   HttpServletRequest request){
        String mobile = logInVO.getMobile();
        String password = logInVO.getPassword();

        Assert.notEmpty(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = request.getRemoteAddr(); // 获取远程地址，用于记录登录日志
        UserInfoVO userInfoVO = userInfoService.login(logInVO, ip);

        return R.ok().message("登录成功").data("userInfo",userInfoVO);
    }

    @Operation(description = "校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);

        if(result){
            return R.ok();
        }else{
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @Operation(description = "校验手机号是否被注册")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(
            @Parameter(description = "手机号")
            @PathVariable String mobile){
        return userInfoService.checkMobile(mobile);
    }

    @Operation(description = "获取首页用户信息")
    @GetMapping("/auth/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserIndexVO userIndexVO = userInfoService.getIndexUserInfo(userId);
        return R.ok().data("userIndexVO", userIndexVO);
    }

}

