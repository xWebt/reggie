package com.webt.reggie_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.webt.reggie_take_out.common.BaseContext;
import com.webt.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截用户在未登录情况下进入后台页面
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/common/**",
                "/front/**",
                "/user/sendMsg", // 移动端发送短信
                "/user/login"    // 移动端登入
        };
        boolean check = checkPath(urls, requestURI);
        //不需要拦截放行
        if (check) {
            log.info("这个请求不需要拦截，请求路径为：{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        boolean checkLogin = request.getSession().getAttribute("employee") != null;
        if (checkLogin) {
            log.info("用户已登录,登录id是：{}", request.getSession().getAttribute("employee"));

            Long currentId = (Long) request.getSession().getAttribute("employee");
            log.info("现在的id是{}", currentId);
            BaseContext.setCurrentId(currentId);
//            long id = Thread.currentThread().getId();
//            log.info("线程id是：{}",id);

            filterChain.doFilter(request, response);
            return;
        }
        // 4-2. 判断登录状态，如果已登录，则直接放行（移动端用户登入）
        if (request.getSession().getAttribute("user") != null) {

            log.info("用户已登入，用户ID为: {}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    /**
     * 检查路径是否需要被拦截
     *
     * @param urls
     * @param path
     * @return
     */
    public boolean checkPath(String[] urls, String path) {
        for (String url : urls) {
            if (antPathMatcher.match(url, path)) {
                return true;
            }
        }
        return false;
    }
}
