//package com.webt.reggie_take_out.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//@Slf4j
//@Configuration
//public class WebMvcConfig extends WebMvcConfigurationSupport {
//
//    /**
//     * 设置静态资源映射,在没有将资源放置在static下时的操作
//     * @param registry
//     */
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        log.info("开始静态资源映射");
//        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
//    }
//}
