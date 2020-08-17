package com.fdzang.micro.common.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author tanghu
 * @Date: 2020/7/9 10:10
 */
@Component
public class RestUtil {

    @Autowired
    private RestTemplate restTemplate;

    public Response restGet(Request request) throws Exception {
        HttpHeaders headers = initialHeader(request);

        if (StringUtils.isEmpty(request.getJsonBody())) {
            request.setJsonBody(JSONObject.toJSONString(request.getBodies()));
        }

        String url = initUrl(request);
        HttpEntity<String> entity = new HttpEntity<>(request.getJsonBody(), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return convert(response);
    }

    public Response restPost(Request request) throws Exception {
        HttpHeaders headers = initialHeader(request);

        if (StringUtils.isEmpty(request.getJsonBody())) {
            request.setJsonBody(JSONObject.toJSONString(request.getBodies()));
        }

        String url = initUrl(request);
        HttpEntity<String> entity = new HttpEntity<>(request.getJsonBody(), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return convert(response);
    }

    public Response restPut(Request request) throws Exception {
        HttpHeaders headers = initialHeader(request);

        if (StringUtils.isEmpty(request.getJsonBody())) {
            request.setJsonBody(JSONObject.toJSONString(request.getBodies()));
        }

        String url = initUrl(request);
        HttpEntity<String> entity = new HttpEntity<>(request.getJsonBody(), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        return convert(response);
    }

    public Response restDelete(Request request) throws Exception {
        HttpHeaders headers = initialHeader(request);

        if (StringUtils.isEmpty(request.getJsonBody())) {
            request.setJsonBody(JSONObject.toJSONString(request.getBodies()));
        }

        String url = initUrl(request);
        HttpEntity<String> entity = new HttpEntity<>(request.getJsonBody(), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        return convert(response);
    }

    /**
     * 初始化Url
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private String initUrl(Request request) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder("http://");
        sbUrl.append(request.getService());
        if (!StringUtils.isBlank(request.getPath())) {
            sbUrl.append(request.getPath());
        }
        if (null != request.getQueries()) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : request.getQueries().entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append(Constant.SPE3_CONNECT);
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append(Constant.SPE4_EQUAL);
                        sbQuery.append(URLEncoder.encode(query.getValue(), Constant.ENCODING));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append(Constant.SPE5_QUESTION).append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private HttpHeaders initialHeader(Request request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                headers.add(header.getKey(), header.getValue());
            }
        }

        return headers;
    }

    private Response convert(ResponseEntity<String> response) {
        Response resp = new Response();

        if (null != response) {
            for (Map.Entry<String, List<String>> headers : response.getHeaders().entrySet()) {
                resp.setHeader(headers.getKey(), headers.getValue().get(0));
            }

            resp.setStatusCode(response.getStatusCodeValue());
            resp.setContentType(resp.getHeader(Constant.CONTENT_TYPE));
            resp.setRequestId(resp.getHeader(Constant.REQUEST_ID));
            resp.setErrorMessage(resp.getHeader(Constant.ERROR_MESSAGE));
            resp.setBody(response.getBody());
        } else {
            //服务器无回应
            resp.setStatusCode(500);
            resp.setErrorMessage("No Response");
        }

        return resp;
    }
}