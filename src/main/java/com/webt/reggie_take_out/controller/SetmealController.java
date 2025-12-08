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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetmealController{
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CateoryService cateoryService;

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
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids : {}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(
                setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status,
                                  @RequestParam("ids") List<Long> ids) {

        log.info("套餐{} 状态修改为：{}", ids, status == 0 ? "停售" : "起售");

        if (ids == null || ids.isEmpty()) {
            return R.error("更改内容不能为空");
        }

        // 直接使用传入的 status 作为要修改的目标状态：0=停售, 1=起售

        // 查询是否存在已经是该状态的套餐
        Integer count = setmealService.lambdaQuery()
                .in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, status)
                .count();

        if (count == ids.size()) {
            return R.error("所选套餐已经全部是该状态，不需要重复操作");
        }

        // 执行更新
        setmealService.lambdaUpdate()
                .in(Setmeal::getId, ids)
                .set(Setmeal::getStatus, status)
                .update();

        return R.success(status == 0 ? "停售成功" : "起售成功");
    }

}
