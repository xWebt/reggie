package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.dto.DishDto;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.entity.Dish;
import com.webt.reggie_take_out.entity.DishFlavor;
import com.webt.reggie_take_out.service.CateoryService;
import com.webt.reggie_take_out.service.DishFlavorService;
import com.webt.reggie_take_out.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
@Api(tags = "菜品管理接口") // 类注解
public class DishController {

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    DishService dishService;

    @Autowired
    CateoryService cateoryService;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public R<String> save(@RequestBody @ApiParam(value = "菜品DTO", required = true) DishDto dishDto) {
        log.info("save dish {}", dishDto);
        dishService.saveWithFlavor(dishDto);
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public R<Page> page(
            @ApiParam(value = "页码", required = true) int page,
            @ApiParam(value = "每页条数", required = true) int pageSize,
            @ApiParam(value = "菜品名称", required = false) String name) {

        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtos = records.stream().map(item -> {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            Long categoryId = item.getCategoryId();
            Category category = cateoryService.getById(categoryId);
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品及口味")
    public R<DishDto> getById(@ApiParam(value = "菜品ID", required = true) @PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        log.info("getById {}", dishDto);
        return R.success(dishDto);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public R<String> update(@RequestBody @ApiParam(value = "菜品DTO", required = true) DishDto dishDto) {
        log.info("update dish {}", dishDto);
        dishService.updateWithFloavor(dishDto);
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);
        return R.success("修改成功");
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public R<String> delete(@ApiParam(value = "菜品ID列表", required = true) @RequestParam("ids") List<Long> ids) {
        log.info("delete dish {}", ids);
        if (ids == null || ids.size() <= 0) {
            return R.error("删除菜品不能为空");
        }
        dishService.removeByIds(ids);
        return R.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    @ApiOperation("批量修改菜品状态")
    public R<String> changeStatus(
            @ApiParam(value = "菜品ID列表", required = true) @RequestParam("ids") List<Long> ids,
            @ApiParam(value = "状态 0=停售, 1=起售", required = true) @PathVariable Integer status) {
        log.info("菜品{}状态修改为：{}", ids, status == 0 ? "停售" : "起售");
        if (ids == null || ids.size() <= 0) {
            return R.error("更改内容不能为空");
        }
        status = status == 0 ? 1 : 0;
        Integer count = dishService.lambdaQuery().in(Dish::getId, ids).eq(Dish::getStatus, status).count();
        if (count < 0) {
            return R.error("所选所有的菜品已经操作完成，不需要重复操作");
        }
        dishService.lambdaUpdate().in(Dish::getId, ids).set(Dish::getStatus, status).update();
        return R.success("停售成功");
    }

    //    @GetMapping("/list")
    //    public R<List<Dish>> list(Dish dish) {
    //        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
    //        queryWrapper.eq(Dish::getStatus,1);
    //
    //        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    //        List<Dish> list =  dishService.list(queryWrapper);
    //        return R.success(list);
    //    }

    @GetMapping("/list")
    @ApiOperation("查询菜品列表（包含口味信息）")
    public R<List<DishDto>> list(@ApiParam(value = "菜品查询条件") Dish dish) {
        //存在redis的缓存当中
        List<DishDto> Dtolist = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        Dtolist = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (Dtolist != null) {
            return R.success(Dtolist);
        }
        //如果缓存当中没有
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        Dtolist =
                list.stream()
                        .map(
                                (item) -> {
                                    DishDto dto = new DishDto();
                                    BeanUtils.copyProperties(item, dto);
                                    Long categoryId = item.getCategoryId();
                                    Category category = cateoryService.getById(categoryId);
                                    if (category != null) {
                                        String categoryName = category.getName();
                                        dto.setCategoryName(categoryName);
                                    }
                                    Long dishId = item.getId();
                                    LambdaQueryWrapper<DishFlavor> dishLambdaQueryWrapper =
                                            new LambdaQueryWrapper<>();
                                    dishLambdaQueryWrapper.eq(DishFlavor::getId, dishId);
                                    List<DishFlavor> dishFlavors = dishFlavorService.list(dishLambdaQueryWrapper);
                                    dto.setFlavors(dishFlavors);
                                    return dto;
                                })
                        .collect(Collectors.toList());
        //不存在的情况下更改缓存，优化多次查询
        redisTemplate.opsForValue().set(key, Dtolist, 60, TimeUnit.MINUTES);
        return R.success(Dtolist);
    }
}
