package com.webt.reggie_take_out.common;

import javax.servlet.http.HttpServletRequest;

/**
 * 基于threadlocal封装的工具类，用户保存和获取当前的用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long userId) {
        threadLocal.set(userId);
    }

    public static Long getCurrentId() {
            return threadLocal.get();
    }
}
