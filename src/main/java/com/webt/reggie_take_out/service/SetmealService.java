package com.webt.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webt.reggie_take_out.dto.SetmealDto;
import com.webt.reggie_take_out.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
  /**
   * 新增套餐，同时保存套餐和菜品关系
   *
   * @param setmealDto
   */
  public void saveWithDish(SetmealDto setmealDto);

  public void removeWithDish(List<Long> ids);
}
