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
 * 用户登录记录表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("user_login_record")
@Schema( name =  "UserLoginRecord对象", description = "用户登录记录表")
public class UserLoginRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "用户id")
    private Long userId;

    @Schema( description = "ip")
    private String ip;

    @Schema( description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
