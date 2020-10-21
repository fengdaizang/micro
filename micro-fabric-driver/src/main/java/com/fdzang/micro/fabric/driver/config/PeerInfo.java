package com.fdzang.micro.fabric.driver.config;

import lombok.Data;

@Data
public class PeerInfo {
    private String name;
    private boolean useTLS;
    private String addr;
    private String tlsPath;
}
