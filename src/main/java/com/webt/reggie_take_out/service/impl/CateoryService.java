package com.webt.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webt.reggie_take_out.entity.Category;
import com.webt.reggie_take_out.entity.Setmeal;

public interface CateoryService extends IService<Category> {
    public void remove(Long id);
}
