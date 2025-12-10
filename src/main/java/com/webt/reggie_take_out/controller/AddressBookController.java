package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.webt.reggie_take_out.common.BaseContext;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.AddressBook;
import com.webt.reggie_take_out.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 地址簿管理 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
@Api(tags = "地址簿管理接口") // Swagger 类注解
public class AddressBookController {

  @Autowired
  private AddressBookService addressBookService;

  @PostMapping
  @ApiOperation(value = "新增地址")
  public R<AddressBook> save(@RequestBody @ApiParam(value = "地址信息", required = true) AddressBook addressBook) {
    addressBook.setUserId(BaseContext.getCurrentId());
    log.info("addressBook:{}", addressBook);
    addressBookService.save(addressBook);
    return R.success(addressBook);
  }

  @PutMapping("default")
  @ApiOperation(value = "设置默认地址")
  public R<AddressBook> setDefault(@RequestBody @ApiParam(value = "地址信息", required = true) AddressBook addressBook) {
    log.info("addressBook:{}", addressBook);
    LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
    wrapper.set(AddressBook::getIsDefault, 0);
    addressBookService.update(wrapper);

    addressBook.setIsDefault(1);
    addressBookService.updateById(addressBook);
    return R.success(addressBook);
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "根据ID查询地址")
  public R<AddressBook> get(@ApiParam(value = "地址ID", required = true) @PathVariable Long id) {
    AddressBook addressBook = addressBookService.getById(id);
    if (addressBook != null) {
      return R.success(addressBook);
    } else {
      return R.error("没有找到该对象");
    }
  }

  @GetMapping("default")
  @ApiOperation(value = "查询默认地址")
  public R<AddressBook> getDefault() {
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
    queryWrapper.eq(AddressBook::getIsDefault, 1);

    AddressBook addressBook = addressBookService.getOne(queryWrapper);

    if (addressBook == null) {
      return R.error("没有找到该对象");
    } else {
      return R.success(addressBook);
    }
  }

  @GetMapping("/list")
  @ApiOperation(value = "查询指定用户的全部地址")
  public R<List<AddressBook>> list(@ApiParam(value = "地址查询条件") AddressBook addressBook) {
    addressBook.setUserId(BaseContext.getCurrentId());
    log.info("addressBook:{}", addressBook);

    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
    queryWrapper.orderByDesc(AddressBook::getUpdateTime);

    return R.success(addressBookService.list(queryWrapper));
  }
}
