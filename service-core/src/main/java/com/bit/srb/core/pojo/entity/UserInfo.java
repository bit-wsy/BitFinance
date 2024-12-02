package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("user_info")
@Schema( name =  "UserInfo对象", description = "用户基本信息")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_LOCKED = 0;
    public static final String AVATAR_DEFAULT = "https://bit-file-shell.oss-cn-beijing.aliyuncs.com/avatar/default.png";

    @Schema( description = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "1：出借人 2：借款人")
    private Integer userType;

    @Schema( description = "手机号")
    private String mobile;

    @Schema( description = "用户密码")
    private String password;

    @Schema( description = "用户昵称")
    private String nickName;

    @Schema( description = "用户姓名")
    private String name;

    @Schema( description = "身份证号")
    private String idCard;

    @Schema( description = "邮箱")
    private String email;

    @Schema( description = "微信用户标识openid")
    private String openid;

    @Schema( description = "头像")
    private String headImg;

    @Schema( description = "绑定状态（0：未绑定，1：绑定成功 -1：绑定失败）")
    private Integer bindStatus;

    @Schema( description = "借款人认证状态（0：未认证 1：认证中 2：认证通过 -1：认证失败）")
    private Integer borrowAuthStatus;

    @Schema( description = "绑定账户协议号")
    private String bindCode;

    @Schema( description = "用户积分")
    private Integer integral;

    @Schema( description = "状态（0：锁定 1：正常）")
    private Integer status;

    @Schema( description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
