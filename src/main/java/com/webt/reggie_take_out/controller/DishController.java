package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.dto.DishDto;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.entity.Dish;
import com.webt.reggie_take_out.service.impl.CateoryService;
import com.webt.reggie_take_out.service.impl.DishFlavorService;
import com.webt.reggie_take_out.service.impl.DishService;
import com.webt.reggie_take_out.service.impl.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    DishService dishService;

    @Autowired
    CateoryService cateoryService;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("save dish {}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name) {
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();


        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtos = records.stream().map((item) ->{
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item,dto);
            Long categoryId = item.getCategoryId();

            Category category = cateoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dto.setCategoryName(categoryName);
            }
            return dto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        log.info("getById {}", dishDto);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("update dish {}", dishDto);
        dishService.updateWithFloavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("delete dish {}", ids);

        if(ids == null && ids.size()  <= 0){
            return R.error("删除菜品不能为空");
        }

        dishService.removeByIds(ids);

        return R.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String>  changeStatus(@RequestParam("ids") List<Long> ids,@PathVariable Integer status) {
        log.info("菜品{}状态修改为：{}", ids, status == 0 ? "停售" : "起售");

        if(ids == null || ids.size() <= 0){
            return R.error("更改内容不能为空");
        }

        status = status == 0 ? 1 : 0;

        Integer count  = dishService.lambdaQuery()
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,status)
                .count();
        if(count < 0){
            return R.error("所选所有的菜品已经操作完成，不需要重复操作");
        }

        dishService.lambdaUpdate()
                .in(Dish::getId,ids)
                .set(Dish::getStatus,status)
                .update();

        //TODO：套餐部分还没写，后面处理

        return R.success("停售成功");
    }
}
