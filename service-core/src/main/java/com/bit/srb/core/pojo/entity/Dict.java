package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@Schema(name = "Dict对象", description = "数据字典")
public class Dict implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema( description = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "上级id")
    private Long parentId;

    @Schema( description = "名称")
    private String name;

    @Schema( description = "值")
    private Integer value;

    @Schema( description = "编码")
    private String dictCode;

    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "删除标记（0:不可用 1:可用）")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    // 说明数据库中没有这个词条
    @TableField(exist = false)
    private Boolean hasChildren;

    @Override
    public String toString() {
        return "Dict{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", dictCode='" + dictCode + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleted=" + deleted +
                ", hasChildren=" + hasChildren +
                '}';
    }
}
