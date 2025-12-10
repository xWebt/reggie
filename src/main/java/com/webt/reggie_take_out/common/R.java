package com.webt.reggie_take_out.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel(value = "统一返回结果对象", description = "通用返回类，用于封装接口返回数据")
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码：1成功，0和其它数字为失败", example = "1")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty(value = "提示信息", example = "操作成功")
    private String msg; //错误信息

    @ApiModelProperty(value = "返回数据")
    private T data; //数据

    @ApiModelProperty(value = "动态数据集合")
    private Map<String, Object> map = new HashMap<>(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
