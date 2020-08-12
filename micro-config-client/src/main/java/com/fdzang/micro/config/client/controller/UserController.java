package com.fdzang.micro.config.client.controller;

import com.fdzang.micro.config.client.domain.UserConfig;
import com.fdzang.micro.config.client.domain.UserValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserValue value;

    @Autowired
    private UserConfig config;

    @GetMapping("/value")
    public String getValue(){
        return value.toString();
    }

    @GetMapping("/config")
    private String getConfig(){
        return config.toString();
    }
}
