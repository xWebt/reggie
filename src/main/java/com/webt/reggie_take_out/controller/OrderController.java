package com.webt.reggie_take_out.controller;

import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.Orders;
import com.webt.reggie_take_out.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
@Api(tags = "订单管理接口") // 类注解
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/submit")
  @ApiOperation("提交订单")
  public R<String> submit(
          @RequestBody @ApiParam(value = "订单信息", required = true) Orders order) {

    log.info("订单数据：{}", order);
    orderService.submit(order);
    return R.success("订单成功提交");
  }
}
