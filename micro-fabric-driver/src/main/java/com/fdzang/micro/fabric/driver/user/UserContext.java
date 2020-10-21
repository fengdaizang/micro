package com.fdzang.micro.fabric.driver.user;

import com.fdzang.micro.fabric.driver.config.FabricConfig;
import com.fdzang.micro.fabric.driver.util.IOUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.*;
import java.security.PrivateKey;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class UserContext implements User, Serializable {
    private static final long serialVersionUID = 975439861376968096L;
    private FabricConfig config;
    private CAEnrollment enrollment;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public UserContext(FabricConfig config) {
        this.config = config;
        this.enrollment = initEnrollment();
    }

    @Override
    public String getName() {
        return config.getUsername();
    }

    @Override
    public Set<String> getRoles() {
        return new HashSet<>();
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return config.getOrgName();
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    @Override
    public String getMspId() {
        return config.getMspId();
    }

    private CAEnrollment initEnrollment() {
        PrivateKey privateKey;
        String cert;
        try {
            //(1)获取privateKey
            String sk = new String(IOUtil.inputStreamToByte(UserContext.class.getClassLoader().getResourceAsStream(config.getEnv() + File.separator + config.getKeyFile())));
            Reader pemReader = new StringReader(sk);
            PEMParser pemParser = new PEMParser(pemReader);
            PrivateKeyInfo pemPair = (PrivateKeyInfo) pemParser.readObject();
            privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

            //(2)获取cert
            cert = new String(IOUtil.inputStreamToByte(UserContext.class.getClassLoader().getResourceAsStream(config.getEnv() + File.separator + config.getCertFile())));

            return new CAEnrollment(privateKey, cert);
        } catch (IOException e) {
            log.error("init enrollment failed", e);

            throw new RuntimeException("init enrollment failed");
        }
    }
}
