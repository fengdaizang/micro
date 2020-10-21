package com.fdzang.micro.fabric.driver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "fabric.config")
public class FabricConfig {
    private String env;
    private String username;
    private String orgName;
    private String mspId;
    private String keyFile;
    private String certFile;
    private List<OrderInfo> order;
    private List<PeerInfo> peer;
}
