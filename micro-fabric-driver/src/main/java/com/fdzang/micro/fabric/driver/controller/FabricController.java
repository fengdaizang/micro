package com.fdzang.micro.fabric.driver.controller;

import com.fdzang.micro.fabric.driver.client.FabricClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class FabricController {

    @Autowired
    private FabricClient client;

    @GetMapping("/query")
    public Object query() throws InvalidArgumentException {
        ArrayList<String> args = new ArrayList<>();
        args.add("a");
        Collection<ProposalResponse> responses =  client.query("mychannel", "mycc", "query", args);

        String result = "";
        System.out.println(responses.size());
        for (ProposalResponse response:responses) {
            result += new String(response.getChaincodeActionResponsePayload());
        }

        return result;
    }

    @GetMapping("/invoke")
    public Object invoke() throws InvalidArgumentException {
        ArrayList<String> args = new ArrayList<>();
        args.add("a");
        args.add("b");
        args.add("10");

        Collection<ProposalResponse> responses =  client.invoke("mychannel", "mycc", "invoke", args);
        String result = "";
        for (ProposalResponse response:responses) {
            result += new String(response.getChaincodeActionResponsePayload());
        }

        return result;
    }
}
