package com.webt.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webt.reggie_take_out.common.CustomException;
import com.webt.reggie_take_out.dto.SetmealDto;
import com.webt.reggie_take_out.entity.Setmeal;
import com.webt.reggie_take_out.entity.SetmealDish;
import com.webt.reggie_take_out.mapper.SetmealMapper;
import com.webt.reggie_take_out.service.SetmealDishService;
import com.webt.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService {
  @Autowired private SetmealDishService setmealDishService;

  /**
   * 新增套餐，同时保存套餐和菜品关系
   *
   * @param setmealDto
   */
  @Transactional
  public void saveWithDish(SetmealDto setmealDto) {
    // 保存套餐基本信息，操作setmeal 执行insert
    this.save(setmealDto);

    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

    setmealDishes.stream()
        .map(
            (item) -> {
              item.setSetmealId(setmealDto.getId());
              return item;
            })
        .collect(Collectors.toList());

    // 保存套餐菜品关系，操作setmeal_dish 执行insert
    setmealDishService.saveBatch(setmealDishes);
  }

  @Override
  @Transactional
  public void removeWithDish(List<Long> ids) {
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
    queryWrapper.in(Setmeal::getId, ids);
    queryWrapper.eq(Setmeal::getStatus, 1);
    int count = this.count(queryWrapper);

    if (count > 0) {
      throw new CustomException("套餐正在售卖当中，不能删除");
    }

    this.removeByIds(ids);
    LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper();
    queryWrapper1.in(SetmealDish::getSetmealId, ids);
    setmealDishService.remove(queryWrapper1);
    return;
  }
}
