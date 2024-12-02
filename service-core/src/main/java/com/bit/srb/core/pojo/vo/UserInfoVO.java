package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息对象")
public class UserInfoVO {
    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "头像")
    private String headImg;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "1：出借人 2：借款人")
    private Integer userType;

    @Schema(description = "JWT访问令牌")
    private String token;
}
