package com.bit.srb.sms.utli;


import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")

public class SmsProperties implements InitializingBean {

    // 按照成员的形式读取yml文件中的内容

    private String regionId;
    private String keyId;
    private String keySecret;
    private String templateCode;
    private String signName;

    public static String REGION_ID;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String TEMPLATE_CODE;
    public static String SIGN_NAME;


    // 来自上面接口的方法，在成员被赋值后自动被调用
    @Override
    public void afterPropertiesSet() throws Exception{
        REGION_ID = regionId;
        KEY_ID = keyId;
        KEY_SECRET = keySecret;
        TEMPLATE_CODE = templateCode;
        SIGN_NAME = signName;
    }
}
