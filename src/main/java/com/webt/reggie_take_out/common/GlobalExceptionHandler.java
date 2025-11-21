package com.webt.reggie_take_out.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;



@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class,Controller.class})
public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class) // 添加一个捕获所有异常的处理器
//    public R<String> handleGenericException(Exception ex) {
//        log.error("捕获到通用未知异常: {}", ex.getMessage(), ex); // 记录堆栈信息
//        return R.error("服务器发生未知错误");
//    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String split = ex.getMessage().split(" ")[2];
            String message =split + "已存在";
            return R.error(message);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
