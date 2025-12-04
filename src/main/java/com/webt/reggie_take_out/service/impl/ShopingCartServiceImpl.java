package com.webt.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webt.reggie_take_out.entity.ShoppingCart;
import com.webt.reggie_take_out.mapper.ShopingCartMapper;
import com.webt.reggie_take_out.service.ShopingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShopingCartServiceImpl extends ServiceImpl<ShopingCartMapper, ShoppingCart>
    implements ShopingCartService {}
