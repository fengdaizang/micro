package com.fdzang.micro.common.http;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghu
 * @Date: 2020/7/9 8:47
 */
@Data
public class Response {
    private int statusCode;
    private String contentType;
    private String requestId;
    private String errorMessage;
    private Map<String, String> headers;
    private String body;

    public String getHeader(String key) {
        if (null != headers) {
            return headers.get(key);
        } else {
            return null;
        }
    }

    public void setHeader(String key, String value) {
        if (null == this.headers) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.put(key, value);
    }
}