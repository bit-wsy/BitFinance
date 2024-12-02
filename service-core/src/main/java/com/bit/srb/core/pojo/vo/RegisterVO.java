package com.bit.srb.core.pojo.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "注册对象")
public class RegisterVO {

    @Schema(description = "用户类型")
    private Integer userType;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "密码")
    private String password;
}
