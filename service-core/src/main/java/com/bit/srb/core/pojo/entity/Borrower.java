package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 借款人
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@Schema(name = "Borrower对象", description = "借款人")
public class Borrower implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "用户id")
    private Long userId;

    @Schema( description = "姓名")
    private String name;

    @Schema( description = "身份证号")
    private String idCard;

    @Schema( description = "手机")
    private String mobile;

    @Schema( description = "性别（1：男 0：女）")
    private Integer sex;

    @Schema( description = "年龄")
    private Integer age;

    @Schema( description = "学历")
    private Integer education;

    @Schema( description = "是否结婚（1：是 0：否）")
    @TableField("is_marry")
    private Boolean marry;

    @Schema( description = "行业")
    private Integer industry;

    @Schema( description = "月收入")
    private Integer income;

    @Schema( description = "还款来源")
    private Integer returnSource;

    @Schema( description = "联系人名称")
    private String contactsName;

    @Schema( description = "联系人手机")
    private String contactsMobile;

    @Schema( description = "联系人关系")
    private Integer contactsRelation;

    @Schema( description = "状态（0：未认证，1：认证中， 2：认证通过， -1：认证失败）")
    private Integer status;

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
