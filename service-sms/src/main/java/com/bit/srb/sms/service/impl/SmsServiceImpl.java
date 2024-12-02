package com.bit.srb.sms.service.impl;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.bit.common.exception.Assert;
import com.bit.common.exception.BusinessException;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.sms.service.SmsService;
import com.bit.srb.sms.utli.SmsProperties;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void send(String mobile, String templateCode, Map<String, Object> param) {
        //手机号，短信模板，参数

        // HttpClient Configuration
        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
                .maxConnections(128) // Set the connection pool size
                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
                // Configure the proxy
                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
                .x509TrustManagers(new X509TrustManager[]{})
                .keyManagers(new KeyManager[]{})
                .ignoreSSL(false)
                .build();*/

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId(SmsProperties.KEY_ID)
                .accessKeySecret(SmsProperties.KEY_SECRET)
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // 把代码参数（验证码）转换成json形式
        Gson gson = new Gson();
        String jsonParam = gson.toJson(param);
        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(mobile)
                .signName(SmsProperties.SIGN_NAME)
                .templateCode(SmsProperties.TEMPLATE_CODE)
                .templateParam(jsonParam)
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();
        // Asynchronously get the return value of the API request
        SendSmsResponse resp = null;
        try {

            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            // Synchronously get the return value of the API request
            resp = response.get();

            String message = new Gson().toJson(resp.getBody().getMessage());
            String code = resp.getBody().getCode();

            log.info("message: " + message);
            log.info("code: " + code);

            // 业务失败处理
            // 判断是否过于频繁
            Assert.notEquals(code, "isv.BUSINESS_LIMIT_CONTROL",ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);

            // 判断是否发送成功
            Assert.equals(code, "OK", ResponseEnum.ALIYUN_SMS_ERROR);

        } catch (ExecutionException e) {
            log.error("阿里云SDK请求调用失败:" + resp.getBody().getMessage() + resp.getStatusCode());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR , e);

        } catch (InterruptedException e) {
            log.error("阿里云SDK请求调用失败:" + resp.getBody().getMessage() + resp.getStatusCode());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR , e);
        }

        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

        // Finally, close the client
        client.close();
    }
}
