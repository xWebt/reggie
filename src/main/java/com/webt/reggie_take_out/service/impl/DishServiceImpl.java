package com.webt.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webt.reggie_take_out.dto.DishDto;
import com.webt.reggie_take_out.entity.Dish;
import com.webt.reggie_take_out.entity.DishFlavor;
import com.webt.reggie_take_out.mapper.DishFlavorMapper;
import com.webt.reggie_take_out.mapper.DishMapper;
import com.webt.reggie_take_out.service.DishFlavorService;
import com.webt.reggie_take_out.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
  @Autowired DishFlavorService dishFlavorService;
  @Autowired private DishFlavorMapper dishFlavorMapper;

  /**
   * 新增菜品同时保存口味数据
   *
   * @param dishDto
   */
  @Override
  @Transactional
  public void saveWithFlavor(DishDto dishDto) {
    this.save(dishDto);
    Long dishId = dishDto.getId();
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors =
        flavors.stream()
            .map(
                item -> {
                  item.setDishId(dishId);
                  return item;
                })
            .collect(Collectors.toList());
    dishFlavorService.saveBatch(flavors);
  }

  @Override
  public DishDto getByIdWithFlavor(Long id) {
    // 查询基本信息，从dish表查询
    Dish dish = this.getById(id);

    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish, dishDto);

    // 查询口味信息，从DishFlavor当中查询
    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, id);
    List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

    dishDto.setFlavors(flavors);
    return dishDto;
  }

  @Override
  public void updateWithFloavor(DishDto dishDto) {
    this.updateById(dishDto);

    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

    dishFlavorService.remove(queryWrapper);
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors =
        flavors.stream()
            .map(
                item -> {
                  item.setDishId(dishDto.getId());
                  return item;
                })
            .collect(Collectors.toList());
    dishFlavorService.saveBatch(dishDto.getFlavors());
  }
}
