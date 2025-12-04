package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.service.CateoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
  @Autowired private CateoryService cateoryService;

  /**
   * 新增分类
   *
   * @param category
   * @return
   */
  @PostMapping
  public R<String> save(@RequestBody Category category) {
    log.info("category: {}", category);
    cateoryService.save(category);
    return R.success("新增分类成功");
  }

  @GetMapping("/page")
  public R<Page> page(Integer page, Integer pageSize) {
    Page<Category> pageinfo = new Page<>(page, pageSize);

    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.orderByAsc(Category::getSort);

    cateoryService.page(pageinfo, queryWrapper);
    return R.success(pageinfo);
  }

  /**
   * 根据id删除分类 log.info("category: {}", category); cateoryService.updateById(category); return
   * R.success("修改分类信
   *
   * @param id
   * @return
   */
  @DeleteMapping
  public R<String> delete(@RequestParam("ids") Long id) {
    log.info("delete分类，id为: {}", id);
    //        cateoryService.removeById(id);
    cateoryService.remove(id);
    return R.success("删除成功");
  }

  @PutMapping
  public R<String> update(@RequestBody Category category) {
    log.info("修改分类信息：{}", category);

    cateoryService.updateById(category);

    return R.success("修改分类信息成功");
  }

  /**
   * 根据条件查询分类数据
   *
   * @param category
   * @return
   */
  @GetMapping("/list")
  public R<List<Category>> list(Category category) {
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(category.getType() != null, Category::getType, category.getType());

    queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

    List<Category> list = cateoryService.list(queryWrapper);
    return R.success(list);
  }
}
