package com.fdzang.micro.common.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tanghu
 * @Date: 2020/7/9 10:27
 */
@Component
public class Client {

    @Autowired
    private RestUtil restUtil;

    public Response execute(Request request) throws Exception {
        if (request.getType() == Type.HTTP) {
            switch (request.getMethod()) {
                case GET:
                    return HttpUtil.httpGet(request);
                case POST:
                    return HttpUtil.httpPost(request);
                case PUT:
                    return HttpUtil.httpPut(request);
                case DELETE:
                    return HttpUtil.httpDelete(request);
                default:
                    throw new IllegalArgumentException(String.format("unsupported method:%s", request.getMethod()));
            }
        } else if (request.getType() == Type.REST) {
            switch (request.getMethod()) {
                case GET:
                    return restUtil.restGet(request);
                case POST:
                    return restUtil.restPost(request);
                case PUT:
                    return restUtil.restPut(request);
                case DELETE:
                    return restUtil.restDelete(request);
                default:
                    throw new IllegalArgumentException(String.format("unsupported method:%s", request.getMethod()));
            }
        } else {
            throw new IllegalArgumentException(String.format("unsupported Type:%s", request.getType()));
        }
    }
}
