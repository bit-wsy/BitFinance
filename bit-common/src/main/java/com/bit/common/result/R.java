package com.bit.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {

    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    // 防止被外部直接调用构造函数
    private R() {}

    public static R ok() {
        R r = new R();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMessage());
        return r;
    }

    public static R error() {
        R r = new R();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMessage());
        return r;
    }

    public static R setResult(ResponseEnum respinseEnum) {
        R r = new R();
        r.setCode(respinseEnum.getCode());
        r.setMessage(respinseEnum.getMessage());
        return r;
    }
    // 注意这几个不是静态方法，因为要设置成员变量
    public R data(String key, Object value) {
        this.data.put(key,value);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }
}
