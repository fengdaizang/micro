package com.fdzang.micro.config.client.domain;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@RefreshScope
@ConfigurationProperties(prefix = "user")
public class UserConfig {
    private String username;

    private String password;

    private String nickname;
}
