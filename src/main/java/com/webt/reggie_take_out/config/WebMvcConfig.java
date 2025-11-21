package com.webt.reggie_take_out.config;

import com.webt.reggie_take_out.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.Http11AprProtocol;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 扩展消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters (List<HttpMessageConverter<?>> converters){
        log.info("扩展消息转换器启动");
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器
        converter.setObjectMapper(new JacksonObjectMapper());

        //将上方的消息转换器追加到mvc框架的转换器集合当中
        converters.add(0,converter);
    }
}
