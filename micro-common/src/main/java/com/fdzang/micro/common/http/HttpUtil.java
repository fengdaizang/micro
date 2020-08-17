package com.fdzang.micro.common.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

public class HttpUtil {

    public static Response httpGet(Request request) throws Exception {
        HttpClient httpClient = wrapClient(request.getHost());

        // 设置超时时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(getTimeout(request.getTimeout()))
                .setConnectionRequestTimeout(getTimeout(request.getTimeout()))
                .setSocketTimeout(getTimeout(request.getTimeout())).build();

        HttpGet httpGet = new HttpGet(initUrl(request));
        httpGet.setConfig(config);

        Map<String, String> headers = request.getHeaders();
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpGet.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
            }
        }

        return convert(httpClient.execute(httpGet));
    }

    public static Response httpPost(Request request)
            throws Exception {
        HttpClient httpClient = wrapClient(request.getHost());

        // 设置超时时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(getTimeout(request.getTimeout()))
                .setConnectionRequestTimeout(getTimeout(request.getTimeout()))
                .setSocketTimeout(getTimeout(request.getTimeout())).build();

        HttpPost httpPost = new HttpPost(request.getHost() + request.getPath());
        httpPost.setConfig(config);

        Map<String, String> headers = request.getHeaders();
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpPost.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
            }
        }

        httpPost.setEntity(new StringEntity(initialJsonBody(request), Constant.ENCODING));

        return convert(httpClient.execute(httpPost));
    }

    public static Response httpPut(Request request) throws Exception {
        HttpClient httpClient = wrapClient(request.getHost());

        // 设置超时时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(getTimeout(request.getTimeout()))
                .setConnectionRequestTimeout(getTimeout(request.getTimeout()))
                .setSocketTimeout(getTimeout(request.getTimeout())).build();

        HttpPut httpPut = new HttpPut(request.getHost() + request.getPath());
        httpPut.setConfig(config);

        Map<String, String> headers = request.getHeaders();
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpPut.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
            }
        }

        httpPut.setEntity(new StringEntity(initialJsonBody(request), Constant.ENCODING));

        return convert(httpClient.execute(httpPut));
    }

    public static Response httpDelete(Request request) throws Exception {
        HttpClient httpClient = wrapClient(request.getHost());

        // 设置超时时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(getTimeout(request.getTimeout()))
                .setConnectionRequestTimeout(getTimeout(request.getTimeout()))
                .setSocketTimeout(getTimeout(request.getTimeout())).build();

        HttpDelete httpDelete = new HttpDelete(request.getHost() + request.getPath());
        httpDelete.setConfig(config);

        Map<String, String> headers = request.getHeaders();
        if (null != headers) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpDelete.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
            }
        }

        return convert(httpClient.execute(httpDelete));
    }

    /**
     * 读取超时时间
     *
     * @param timeout
     * @return
     */
    private static int getTimeout(int timeout) {
        if (timeout == 0) {
            return Constant.DEFAULT_TIMEOUT;
        }

        return timeout;
    }

    /**
     * 初始化Url
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String initUrl(Request request) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(request.getHost());
        if (!StringUtils.isBlank(request.getPath())) {
            sbUrl.append(request.getPath());
        }
        if (null != request.getQueries()) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : request.getQueries().entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append(Constant.SPE3_CONNECT);
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append(Constant.SPE4_EQUAL);
                        sbQuery.append(URLEncoder.encode(query.getValue(), Constant.ENCODING));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append(Constant.SPE5_QUESTION).append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    /**
     * 请求体使用JsonBody
     *
     * @param request
     * @return
     */
    public static String initialJsonBody(Request request) {
        if (StringUtils.isNotBlank(request.getJsonBody())) {
            return request.getJsonBody();
        }

        return JSONObject.toJSONString(request.getBodies());
    }

    /**
     * 转换请求结果
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static Response convert(HttpResponse response) throws IOException {
        Response resp = new Response();

        if (null != response) {
            for (Header header : response.getAllHeaders()) {
                resp.setHeader(header.getName(), MessageDigestUtil.iso88591ToUtf8(header.getValue()));
            }

            resp.setStatusCode(response.getStatusLine().getStatusCode());
            resp.setContentType(resp.getHeader(Constant.CONTENT_TYPE));
            resp.setRequestId(resp.getHeader(Constant.REQUEST_ID));
            resp.setErrorMessage(resp.getHeader(Constant.ERROR_MESSAGE));
            resp.setBody(readStreamAsStr(response.getEntity().getContent()));

        } else {
            //服务器无回应
            resp.setStatusCode(500);
            resp.setErrorMessage("No Response");
        }

        return resp;
    }

    /**
     * 将流转换为字符串
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String readStreamAsStr(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel dest = Channels.newChannel(bos);
        ReadableByteChannel src = Channels.newChannel(is);
        ByteBuffer bb = ByteBuffer.allocate(4096);

        while (src.read(bb) != -1) {
            bb.flip();
            dest.write(bb);
            bb.clear();
        }
        src.close();
        dest.close();

        return new String(bos.toByteArray(), Constant.ENCODING);
    }

    private static HttpClient wrapClient(String host) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        if (host.startsWith("https://")) {
            return sslClient();
        }

        return httpClient;
    }

    private static HttpClient sslClient() {
        try {
            // 在调用SSL之前需要重写验证方法，取消检测SSL
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                }
            };
            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[]{trustManager}, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            // 创建Registry
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig).build();
            return closeableHttpClient;
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}