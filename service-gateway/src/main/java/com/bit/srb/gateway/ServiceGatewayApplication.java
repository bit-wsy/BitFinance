package com.bit.srb.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
//@EnableFeignClients
public class ServiceGatewayApplication {
    public static void main(String[] args){
        try {
            SpringApplication.run(ServiceGatewayApplication.class,args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
