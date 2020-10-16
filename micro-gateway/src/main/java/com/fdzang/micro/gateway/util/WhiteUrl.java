package com.fdzang.micro.gateway.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tanghu
 * @Date: 2019/11/6 16:06
 */
@Component
@ConfigurationProperties("auth.gateway")
public class WhiteUrl {
    private static List<String> white;

    public static List<String> getWhite() {
        return white;
    }

    public void setWhite(List<String> white) {
        this.white = white;
    }
}