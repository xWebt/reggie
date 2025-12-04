package com.webt.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webt.reggie_take_out.common.CustomException;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.entity.Dish;
import com.webt.reggie_take_out.entity.Setmeal;
import com.webt.reggie_take_out.mapper.CateoryMapper;
import com.webt.reggie_take_out.service.CateoryService;
import com.webt.reggie_take_out.service.DishService;
import com.webt.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CateoryServiceImpl extends ServiceImpl<CateoryMapper, Category>
    implements CateoryService {
  @Autowired private DishService dishService;

  @Autowired private SetmealService setmealService;

  /**
   * 根据id删除分类，删除前需要检查
   *
   * @param id
   */
  @Override
  public void remove(Long id) {
    LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
    dishQueryWrapper.eq(Dish::getCategoryId, id);
    int count = dishService.count(dishQueryWrapper);
    if (count > 0) {
      // 已经有菜品关联了，抛出异常
      throw new CustomException("当下分类关联了菜品，不能删除");
    }

    LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
    setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
    int count2 = setmealService.count(setmealQueryWrapper);
    if (count2 > 0) {
      // 已经有套餐关联了，抛出异常
      throw new CustomException("当下分类关联了套餐，不能删除");
    }
    // 正常删除
    super.removeById(id);
  }
}
