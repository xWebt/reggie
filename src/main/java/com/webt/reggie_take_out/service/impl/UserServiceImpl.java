package com.webt.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webt.reggie_take_out.entity.User;
import com.webt.reggie_take_out.mapper.UserMapper;
import com.webt.reggie_take_out.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
