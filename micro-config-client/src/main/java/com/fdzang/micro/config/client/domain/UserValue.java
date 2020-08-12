package com.fdzang.micro.config.client.domain;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
public class UserValue {
    @Value("${user.username}")
    private String username;

    @Value("${user.password}")
    private String password;

    @Value("${user.nickname}")
    private String nickname;

}
