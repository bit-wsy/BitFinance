package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户绑定表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("user_bind")
@Schema( name =  "UserBind对象", description = "用户绑定表")
public class UserBind implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "用户id")
    private Long userId;

    @Schema( description = "用户姓名")
    private String name;

    @Schema( description = "身份证号")
    private String idCard;

    @Schema( description = "银行卡号")
    private String bankNo;

    @Schema( description = "银行类型")
    private String bankType;

    @Schema( description = "手机号")
    private String mobile;

    @Schema( description = "绑定账户协议号")
    private String bindCode;

    @Schema( description = "状态")
    private Integer status;

    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
