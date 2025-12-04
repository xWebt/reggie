package com.webt.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webt.reggie_take_out.dto.DishDto;
import com.webt.reggie_take_out.entity.Dish;

public interface DishService extends IService<Dish> {
  // 新增菜品，插入菜品对应口味
  public void saveWithFlavor(DishDto dto);

  // 根据id查询菜品信息和对应的口味信息
  public DishDto getByIdWithFlavor(Long id);

  void updateWithFloavor(DishDto dishDto);
}
