package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.service.CateoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
@Api(tags = "分类管理接口") // Swagger 类注解
public class CategoryController {

  @Autowired
  private CateoryService cateoryService;

  @PostMapping
  @ApiOperation(value = "新增分类")
  public R<String> save(@RequestBody @ApiParam(value = "分类信息", required = true) Category category) {
    log.info("category: {}", category);
    cateoryService.save(category);
    return R.success("新增分类成功");
  }

  @GetMapping("/page")
  @ApiOperation(value = "分页查询分类")
  public R<Page<Category>> page(
          @ApiParam(value = "页码", required = true) @RequestParam Integer page,
          @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {

    Page<Category> pageinfo = new Page<>(page, pageSize);
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.orderByAsc(Category::getSort);

    cateoryService.page(pageinfo, queryWrapper);
    return R.success(pageinfo);
  }

  @DeleteMapping
  @ApiOperation(value = "删除分类")
  public R<String> delete(@ApiParam(value = "分类ID", required = true) @RequestParam("ids") Long id) {
    log.info("delete分类，id为: {}", id);
    cateoryService.remove(id);
    return R.success("删除成功");
  }

  @PutMapping
  @ApiOperation(value = "修改分类信息")
  public R<String> update(@RequestBody @ApiParam(value = "分类信息", required = true) Category category) {
    log.info("修改分类信息：{}", category);
    cateoryService.updateById(category);
    return R.success("修改分类信息成功");
  }

  @GetMapping("/list")
  @ApiOperation(value = "根据条件查询分类列表")
  public R<List<Category>> list(@ApiParam(value = "查询条件") Category category) {
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
    queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

    List<Category> list = cateoryService.list(queryWrapper);
    return R.success(list);
  }
}
