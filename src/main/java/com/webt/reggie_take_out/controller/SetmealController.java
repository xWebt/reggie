package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.dto.SetmealDto;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.entity.Setmeal;
import com.webt.reggie_take_out.service.CateoryService;
import com.webt.reggie_take_out.service.SetmealDishService;
import com.webt.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetmealController {
  @Autowired private SetmealService setmealService;
  @Autowired private SetmealDishService setmealDishService;

  @Autowired private CateoryService cateoryService;

  @PostMapping
  public R<String> save(@RequestBody SetmealDto setmealDto) {
    log.info("套餐信息: {}", setmealDto);
    setmealService.saveWithDish(setmealDto);
    return R.success("新增套餐成功");
  }

  /**
   * 套餐分页查询
   *
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public R<Page> page(Integer page, Integer pageSize, String name) {
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> dtoPage = new Page<>();

    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name != null, Setmeal::getName, name);

    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    setmealService.page(pageInfo, queryWrapper);

    BeanUtils.copyProperties(pageInfo, dtoPage, "records");
    List<Setmeal> records = pageInfo.getRecords();

    List<SetmealDto> list =
        records.stream()
            .map(
                (item) -> {
                  Long id = item.getCategoryId();
                  SetmealDto dto = new SetmealDto();
                  BeanUtils.copyProperties(item, dto);
                  Category cateory = cateoryService.getById(id);
                  if (cateory != null) {
                    String categoryName = cateory.getName();
                    dto.setCategoryName(categoryName);
                  }
                  return dto;
                })
            .collect(Collectors.toList());

    dtoPage.setRecords(list);
    return R.success(dtoPage);
  }

  @DeleteMapping
  public R<String> delete(@RequestParam List<Long> ids) {
    log.info("ids : {}", ids);
    setmealService.removeWithDish(ids);
    return R.success("删除成功");
  }

  @GetMapping("/list")
  public R<List<Setmeal>> list(Setmeal setmeal) {
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(
        setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    List<Setmeal> list = setmealService.list(queryWrapper);
    return R.success(list);
  }
}
