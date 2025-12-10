package com.webt.reggie_take_out.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单实体")
public class Orders implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "订单号")
  private String number;

  @Schema(description = "订单状态 1待付款 2待派送 3已派送 4已完成 5已取消")
  private Integer status;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "地址ID")
  private Long addressBookId;

  @Schema(description = "下单时间")
  private LocalDateTime orderTime;

  @Schema(description = "结账时间")
  private LocalDateTime checkoutTime;

  @Schema(description = "支付方式 1微信 2支付宝")
  private Integer payMethod;

  @Schema(description = "实收金额")
  private BigDecimal amount;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "用户名")
  private String userName;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "收货地址")
  private String address;

  @Schema(description = "收货人")
  private String consignee;
}
