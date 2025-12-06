package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.webt.reggie_take_out.common.BaseContext;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.ShoppingCart;
import com.webt.reggie_take_out.mapper.ShopingCartMapper;
import com.webt.reggie_take_out.service.ShopingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShopingCartService shopingCartService;
    @Autowired
    private ShopingCartMapper shopingCartMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("add shopping cart {}", shoppingCart);

        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shopingCartService.getOne(queryWrapper);
        if (one != null) {
            Integer number = one.getNumber();
            shoppingCart.setNumber(number + 1);
            shopingCartService.updateById(shoppingCart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shopingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查询购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shopingCartService.list(queryWrapper);

        return R.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shopingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
