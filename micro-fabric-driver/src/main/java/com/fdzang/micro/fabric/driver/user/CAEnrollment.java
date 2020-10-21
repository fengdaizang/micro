package com.fdzang.micro.fabric.driver.user;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.security.PrivateKey;

public class CAEnrollment implements Enrollment, Serializable {
	private static final long serialVersionUID = 550416591376968096L;
	private PrivateKey key;
	private String cert;

	public CAEnrollment(PrivateKey key, String signedPem) {
		this.key = key;
		this.cert = signedPem;
	}

	public PrivateKey getKey() {
		return key;
	}

	public String getCert() {
		return cert;
	}
}
