package com.fdzang.micro.common.jsonfmt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonFmtHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    //自定义key
    private static final String KEY = "X-Micro-JsonBody-Key";
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonFmt.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonFmt jsonFmt = parameter.getParameterAnnotation(JsonFmt.class);
        JSONObject jsonObject = getJsonObject(webRequest);

        String name = getParamName(parameter, jsonFmt);
        Object value = getParamValue(jsonObject, name);


        boolean require = jsonFmt.require();
        if (value == null && require) {
            throw new Exception("parameter[" + name + "]不能为空。");
        }
        if (value == null) {
            return null;
        }

        //对包装类进行转换
        if (value.getClass().equals(JSONArray.class)) {
            Type type = parameter.getGenericParameterType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Class targetClass = (Class) types[0];

            List<Object> list = JSON.parseArray(value.toString(), Object.class);
            List<Object> result = new ArrayList<>();
            for (Object obj : list) {
                if (obj.getClass().equals(JSONObject.class)) {
                    result.add(objectMapper.readValue(obj.toString(), targetClass));
                } else {
                    result.add(obj);
                }
            }

            return result;
        }

        if (value.getClass().equals(JSONObject.class)) {
            value = objectMapper.readValue(value.toString(), parameter.getParameterType());
        }

        return value;
    }

    private String getParamName(MethodParameter parameter, JsonFmt jsonFmt) {
        String value = jsonFmt.value();
        if (StringUtils.isEmpty(value)) {
            value = parameter.getParameterName();
        }
        return value;
    }

    private Object getParamValue(JSONObject jsonObject, String value) {
        for (String key : jsonObject.keySet()) {
            if (key.equalsIgnoreCase(value)) {
                return jsonObject.get(key);
            }
        }
        return null;
    }

    private JSONObject getJsonObject(NativeWebRequest webRequest) throws Exception {
        String jsonBody = (String) webRequest.getAttribute(KEY, NativeWebRequest.SCOPE_REQUEST);
        if (StringUtils.isEmpty(jsonBody)) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int rd;
            while ((rd = reader.read(buf)) != -1) {
                sb.append(buf, 0, rd);
            }

            jsonBody = sb.toString();

            //简单支持form传参
            if (StringUtils.isEmpty(jsonBody)) {
                Map<String, String[]> params = request.getParameterMap();

                Map tmp = new HashMap();
                for (Map.Entry<String, String[]> param : params.entrySet()) {
                    if (param.getValue().length == 1) {
                        tmp.put(param.getKey(), param.getValue()[0]);
                    } else {
                        tmp.put(param.getKey(), param.getValue());
                    }

                }
                jsonBody = JSON.toJSONString(tmp);
            }

            webRequest.setAttribute(KEY, jsonBody, NativeWebRequest.SCOPE_REQUEST);
        }

        return JSONObject.parseObject(jsonBody);
    }
}