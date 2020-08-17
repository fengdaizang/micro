package com.fdzang.micro.common.http;

import lombok.Data;

import java.util.Map;

/**
 * @author tanghu
 * @Date: 2020/7/8 13:59
 */
@Data
public class Request {
    /**
     *（必选）请求的类型，Http请求/Rest请求
     */
    private Type type;
    /*
     * （可选）Host：如，http://-Http模式必填
     */
    private String host;
    /*
     * （可选）Service-Rest模式必填
     */
    private String service;
    /**
     * （必选）Path
     */
    private String path;
    /**
     *（必选）Method
     */
    private Method method;
    /**
     * （必选）超时时间,单位毫秒,设置零默认使用Constant.DEFAULT_TIMEOUT
     */
    private int timeout;
    /**
     * （可选） HTTP头
     */
    private Map<String, String> headers;
    /**
     * （可选）表单参数
     */
    private Map<String, Object> bodies;
    /**
     * （可选）字符串Body体
     */
    private String jsonBody;
    /**
     * （可选）请求参数参数
     */
    private Map<String, String> queries;
}
