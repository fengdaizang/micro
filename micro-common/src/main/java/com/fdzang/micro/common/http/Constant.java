package com.fdzang.micro.common.http;

/**
 * @author tanghu
 * @Date: 2020/2/20 9:21
 */
public class Constant {

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_MD5 = "Content-MD5";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String USER_AGENT = "User-Agent";
    public static final String REQUEST_ID = "X-SPDB-RequestId-Header";
    public static final String ERROR_MESSAGE = "X-SPDB-ErrorMessage-Header";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";

    //默认请求超时时间,单位毫秒
    public static final int DEFAULT_TIMEOUT = 5000;

    //编码UTF-8
    public static final String ENCODING = "UTF-8";

    //串联符
    public static final String SPE1_COMMA = ",";
    //示意符
    public static final String SPE2_COLON = ":";
    //连接符
    public static final String SPE3_CONNECT = "&";
    //赋值符
    public static final String SPE4_EQUAL = "=";
    //问号符
    public static final String SPE5_QUESTION = "?";
}
