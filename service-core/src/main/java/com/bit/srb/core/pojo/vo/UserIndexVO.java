package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserIndexVO {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "1：出借人 2：借款人")
    private Integer userType;

    @Schema(description = "用户头像")
    private String headImg;

    @Schema(description = "绑定状态（0：未绑定，1：绑定成功 -1：绑定失败）")
    private Integer bindStatus;

    @Schema(description = "帐户可用余额")
    private BigDecimal amount;

    @Schema(description = "冻结金额")
    private BigDecimal freezeAmount;

    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;
}
