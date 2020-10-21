package com.fdzang.micro.fabric.driver.client;

import com.fdzang.micro.fabric.driver.config.FabricConfig;
import com.fdzang.micro.fabric.driver.config.OrderInfo;
import com.fdzang.micro.fabric.driver.config.PeerInfo;
import com.fdzang.micro.fabric.driver.user.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class FabricClient {
    @Autowired
    private FabricConfig config;

    private HFClient client;
    private Map<String, Channel> channelMap = new HashMap<>();

    /**
     * 链码查询
     *
     * @param channelName
     * @param chaincodeName
     * @param funcName
     * @param args
     * @return
     */
    public Collection<ProposalResponse> query(String channelName, String chaincodeName, String funcName, ArrayList<String> args) {
        try {
            //1.初始化客户端
            HFClient client = initializeClient();
            //2.初始化通道
            Channel channel = initializeChannel(client, channelName);

            //3.拼装chaincode请求参数
            QueryByChaincodeRequest request = client.newQueryProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
            request.setChaincodeID(ccid);
            request.setFcn(funcName);
            if (CollectionUtils.isNotEmpty(args)) {
                request.setArgs(args);
            }
            request.setProposalWaitTime(3000);

            //4.调用链码查询
            Collection<ProposalResponse> responses = channel.queryByChaincode(request);

            return responses;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 链码调用
     *
     * @param channelName
     * @param chaincodeName
     * @param funcName
     * @param args
     * @return
     */
    public Collection<ProposalResponse> invoke(String channelName, String chaincodeName, String funcName, ArrayList<String> args) {
        try {
            //1.初始化客户端
            HFClient client = initializeClient();
            //2.初始化通道
            Channel channel = initializeChannel(client, channelName);

            //3.拼装chaincode请求参数
            TransactionProposalRequest request = client.newTransactionProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
            request.setChaincodeID(ccid);
            request.setFcn(funcName);
            if (CollectionUtils.isNotEmpty(args)) {
                request.setArgs(args);
            }
            request.setProposalWaitTime(1000);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            tm2.put("result", ":)".getBytes(UTF_8));
            request.setTransientMap(tm2);

            //4.模拟调用链码请求
            Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());

            //5. 发送交易到排序节点进行排序
            channel.sendTransaction(responses);

            return responses;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 初始化客户端
     *
     * @return
     * @throws CryptoException
     * @throws InvalidArgumentException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private HFClient initializeClient() throws CryptoException, InvalidArgumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (client == null) {
            synchronized (FabricClient.class) {
                if (client == null) {
                    UserContext userContext = new UserContext(config);

                    client = HFClient.createNewInstance();
                    client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                    client.setUserContext(userContext);
                }
            }
        }

        return client;
    }

    /**
     * 初始化Channel
     *
     * @param client
     * @param channelName
     * @return
     * @throws InvalidArgumentException
     * @throws TransactionException
     */
    private Channel initializeChannel(HFClient client, String channelName) throws InvalidArgumentException, TransactionException {
        if (!channelMap.containsKey(channelName)) {
            synchronized (FabricClient.class) {
                if (!channelMap.containsKey(channelName)) {
                    Channel channel = client.newChannel(channelName);

                    for (OrderInfo o : config.getOrder()) {
                        channel.addOrderer(covertOrderer(client, o));
                    }
                    for (PeerInfo p : config.getPeer()) {
                        channel.addPeer(covertPeer(client, p));
                    }
                    channel.initialize();

                    channelMap.put(channelName, channel);
                }
            }
        }

        return channelMap.get(channelName);
    }

    /**
     * 转换Orderer信息
     *
     * @param client
     * @param o
     * @return
     * @throws InvalidArgumentException
     */
    private Orderer covertOrderer(HFClient client, OrderInfo o) throws InvalidArgumentException {
        Properties prop = new Properties();
        prop.setProperty("sslProvider", "openSSL");
        prop.setProperty("pemFile", o.getTlsPath());
        prop.setProperty("hostnameOverride", o.getName());
        prop.setProperty("trustServerCertificate", "true");
        prop.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 10 * 1024 * 1024);

        String url = "";
        if (o.isUseTLS()) {
            prop.setProperty("negotiationType", "TLS");
            url = "grpcs://" + o.getAddr();
            File ordererCert1 = Paths.get(o.getTlsPath()).toFile();
            prop.setProperty("pemFile", ordererCert1.getAbsolutePath());
        } else {
            url = "grpc://" + o.getAddr();
        }

        return client.newOrderer(o.getAddr(), url, prop);
    }

    /**
     * 转换Peer信息
     *
     * @param client
     * @param p
     * @return
     * @throws InvalidArgumentException
     */
    private Peer covertPeer(HFClient client, PeerInfo p) throws InvalidArgumentException {
        Properties prop = new Properties();
        prop.setProperty("sslProvider", "openSSL");
        prop.setProperty("pemFile", p.getTlsPath());
        prop.setProperty("hostnameOverride", p.getName());
        prop.setProperty("trustServerCertificate", "true");
        prop.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 10 * 1024 * 1024);

        String url = "";
        if (p.isUseTLS()) {
            prop.setProperty("negotiationType", "TLS");
            url = "grpcs://" + p.getAddr();
            File peerCert = Paths.get(p.getTlsPath()).toFile();
            prop.setProperty("pemFile", peerCert.getAbsolutePath());
        } else {
            url = "grpc://" + p.getAddr();
        }

        return client.newPeer(p.getAddr(), url, prop);
    }
}
