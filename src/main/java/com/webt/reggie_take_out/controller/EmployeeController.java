package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.Employee;
import com.webt.reggie_take_out.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
@Api(tags = "员工管理接口") // 类注解
public class EmployeeController {

  @Autowired
  EmployeeService employeeService;

  @PostMapping("/login")
  @ApiOperation("员工登录")
  public R<Employee> login(
          HttpServletRequest request,
          @RequestBody @ApiParam(value = "登录员工信息", required = true) Employee employee) {

    String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Employee::getUsername, employee.getUsername());
    Employee employee1 = employeeService.getOne(queryWrapper); // 查询一条数据

    if (employee1 == null) {
      log.info("没查询到用户信息");
      return R.error("登录失败");
    }
    if (!employee1.getPassword().equals(password)) {
      log.info("密码错误");
      return R.error("密码错误");
    }
    if (employee1.getStatus() == 0) {
      return R.error("账户已禁用");
    }
    request.getSession().setAttribute("employee", employee1.getId());

    return R.success(employee1);
  }

  @PostMapping("/logout")
  @ApiOperation("员工登出")
  public R<String> logout(HttpServletRequest request) {
    request.getSession().removeAttribute("employee");
    return R.success("退出成功");
  }

  @PostMapping
  @ApiOperation("新增员工")
  public R<String> saveEmployee(
          HttpServletRequest request,
          @RequestBody @ApiParam(value = "员工信息", required = true) Employee employee) {

    log.info("新增员工，员工信息：{}", employee.toString());
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

    Long employeeId = (Long) request.getSession().getAttribute("employee");
    // 可以设置创建者和修改者，保留原注释
    // employee.setCreateUser(employeeId);
    // employee.setUpdateUser(employeeId);

    employeeService.save(employee);

    return R.success("新增成员成功");
  }

  @GetMapping("/page")
  @ApiOperation("分页查询员工信息")
  public R<Page> page(
          @ApiParam(value = "页码", required = true) int page,
          @ApiParam(value = "每页条数", required = true) int pageSize,
          @ApiParam(value = "员工姓名", required = false) String name) {

    log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

    Page pageInfo = new Page(page, pageSize);

    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
    queryWrapper.orderByDesc(Employee::getUpdateTime);

    employeeService.page(pageInfo, queryWrapper);

    return R.success(pageInfo);
  }

  @PutMapping
  @ApiOperation("修改员工信息")
  public R<String> updateEmployee(
          HttpServletRequest request,
          @RequestBody @ApiParam(value = "员工信息", required = true) Employee employee) {

    log.info(employee.toString());
    // 可以设置更新时间和修改者，保留原注释
    // Long userId = (Long)request.getSession().getAttribute("employee");
    // employee.setUpdateTime(LocalDateTime.now());
    // employee.setUpdateUser(userId);

    employeeService.updateById(employee);

    return R.success("员工信息修改成功");
  }

  @GetMapping("/{id}")
  @ApiOperation("根据ID查询员工信息")
  public R<Employee> getById(
          @ApiParam(value = "员工ID", required = true) @PathVariable Long id) {

    log.info("根据id查询员工信息");
    Employee employee = employeeService.getById(id);
    if (employee == null) {
      return R.error("没有查询到员工信息");
    }
    return R.success(employee);
  }
}
