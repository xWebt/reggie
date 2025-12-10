package com.webt.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.webt.reggie_take_out.common.R;
import com.webt.reggie_take_out.entity.User;
import com.webt.reggie_take_out.service.UserService;
import com.webt.reggie_take_out.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口") // 类注解
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    @ApiOperation("发送手机验证码")
    public R<String> sendMsg(
            @RequestBody @ApiParam(value = "用户信息对象", required = true) User user,
            HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);

            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("移动端用户登录")
    public R<User> login(
            @RequestBody @ApiParam(value = "包含手机号和验证码的Map", required = true) Map map,
            HttpSession session) {
        log.info(map.toString());

        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(userLambdaQueryWrapper);

            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            redisTemplate.delete(phone);
            log.info("session = {}", session.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    @ApiOperation("用户退出登录")
    public R<String> logout(HttpServletRequest request) {
        // 清理session中的用户id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
