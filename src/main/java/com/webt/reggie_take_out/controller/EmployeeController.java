package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.Employee;
import com.webt.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
  @Autowired EmployeeService employeeService;

  @PostMapping("/login")
  public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
    //        String password = employee.getPassword();
    //        DigestUtils.md5DigestAsHex(password.getBytes());

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
  public R<String> logout(HttpServletRequest request) {
    request.getSession().removeAttribute("employee");
    return R.success("退出成功");
  }

  @PostMapping
  public R<String> saveEmployee(HttpServletRequest request, @RequestBody Employee employee) {
    log.info("新增员工，员工信息：{}", employee.toString());
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
    //        employee.setCreateTime(LocalDateTime.now());
    //        employee.setUpdateTime(LocalDateTime.now());

    Long employeeId = (Long) request.getSession().getAttribute("employee");

    //        employee.setCreateUser(employeeId);
    //        employee.setUpdateUser(employeeId);

    employeeService.save(employee);

    return R.success("新增成员成功");
  }

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

    Page pageInfo = new Page(page, pageSize);

    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

    queryWrapper.orderByDesc(Employee::getUpdateTime);

    employeeService.page(pageInfo, queryWrapper);

    return R.success(pageInfo);
  }

  /**
   * 根据id修改员工信息
   *
   * @param request
   * @param employee
   * @return
   */
  @PutMapping
  public R<String> updateEmployee(HttpServletRequest request, @RequestBody Employee employee) {
    log.info(employee.toString());
    //
    //        Long userId = (Long)request.getSession().getAttribute("employee");
    //        employee.setUpdateTime(LocalDateTime.now());
    //        employee.setUpdateUser(userId);
    employeeService.updateById(employee);

    return R.success("员工信息修改成功");
  }

  /**
   * 根据id查询员工信息
   *
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<Employee> getById(@PathVariable Long id) {
    log.info("根据id查询员工信息");
    Employee employee = employeeService.getById(id);
    if (employee == null) {
      return R.error("没有查询到员工信息");
    }
    return R.success(employee);
  }
}
