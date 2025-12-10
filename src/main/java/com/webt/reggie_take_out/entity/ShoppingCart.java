package com.webt.reggie_take_out.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 购物车 */
@Data
@Schema(description = "购物车实体")
public class ShoppingCart implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "名称（菜品名 / 套餐名）")
  private String name;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "菜品ID")
  private Long dishId;

  @Schema(description = "套餐ID")
  private Long setmealId;

  @Schema(description = "菜品口味")
  private String dishFlavor;

  @Schema(description = "数量")
  private Integer number;

  @Schema(description = "金额")
  private BigDecimal amount;

  @Schema(description = "图片地址")
  private String image;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;
}
