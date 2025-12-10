package com.webt.reggie_take_out.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系
 */
@Data
@Schema(description = "套餐菜品关系实体")
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "套餐ID")
    private Long setmealId;

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "菜品名称（冗余字段）")
    private String name;

    @Schema(description = "菜品原价")
    private BigDecimal price;

    @Schema(description = "菜品份数")
    private Integer copies;

    @Schema(description = "排序字段")
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建用户ID")
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新用户ID")
    private Long updateUser;

    @Schema(description = "是否删除 0否 1是")
    private Integer isDeleted;
}
