package com.fdzang.micro.common.entity;

import lombok.Data;

@Data
public class ApiResult<T> {
    private long code;
    private String msg;
    private T data;
}
