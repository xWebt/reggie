package com.webt.reggie_take_out.controller;

import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.Orders;
import com.webt.reggie_take_out.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
  @Autowired private OrderService orderService;

  @PostMapping("/submit")
  public R<String> submit(Orders order) {
    log.info("订单数据：{}", order);
    orderService.submit(order);
    return R.success("订单成功提交");
  }
}
