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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("setmeal")
@Api(tags = "套餐管理接口") // Swagger 类注解
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CateoryService cateoryService;

    @PostMapping
    @ApiOperation(value = "新增套餐") // 方法说明
    public R<String> save(@RequestBody @ApiParam(value = "套餐信息", required = true) SetmealDto setmealDto) {
        log.info("套餐信息: {}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询")
    public R<Page<SetmealDto>> page(
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize,
            @ApiParam(value = "套餐名称", required = false) @RequestParam(required = false) String name) {

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<SetmealDto> list = pageInfo.getRecords().stream().map(item -> {
            Long id = item.getCategoryId();
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);
            Category category = cateoryService.getById(id);
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    @ApiOperation(value = "删除套餐")
    public R<String> delete(
            @ApiParam(value = "套餐ID集合", required = true) @RequestParam List<Long> ids) {
        log.info("ids : {}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @ApiOperation(value = "根据条件查询套餐列表")
    public R<List<Setmeal>> list(@ApiParam(value = "套餐查询条件") Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation(value = "批量修改套餐状态")
    public R<String> changeStatus(
            @ApiParam(value = "状态 0=停售, 1=起售", required = true) @PathVariable Integer status,
            @ApiParam(value = "套餐ID集合", required = true) @RequestParam("ids") List<Long> ids) {

        log.info("套餐{} 状态修改为：{}", ids, status == 0 ? "停售" : "起售");

        if (ids == null || ids.isEmpty()) {
            return R.error("更改内容不能为空");
        }

        Integer count = setmealService.lambdaQuery()
                .in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, status)
                .count();

        if (count == ids.size()) {
            return R.error("所选套餐已经全部是该状态，不需要重复操作");
        }

        setmealService.lambdaUpdate()
                .in(Setmeal::getId, ids)
                .set(Setmeal::getStatus, status)
                .update();

        return R.success(status == 0 ? "停售成功" : "起售成功");
    }
}
