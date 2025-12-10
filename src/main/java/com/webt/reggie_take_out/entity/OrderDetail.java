package com.webt.reggie_take_out.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细
 */
@Data
@ApiModel(description = "订单明细实体类")
public class OrderDetail implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty("订单明细ID")
  private Long id;

  @ApiModelProperty("商品名称")
  private String name;

  @ApiModelProperty("订单ID")
  private Long orderId;

  @ApiModelProperty("菜品ID")
  private Long dishId;

  @ApiModelProperty("套餐ID")
  private Long setmealId;

  @ApiModelProperty("菜品口味")
  private String dishFlavor;

  @ApiModelProperty("数量")
  private Integer number;

  @ApiModelProperty("金额")
  private BigDecimal amount;

  @ApiModelProperty("图片地址")
  private String image;
}
