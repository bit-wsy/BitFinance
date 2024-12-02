package com.bit.srb.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.bit.srb", "com.bit.common"})
public class ServiceOssApplication {

    public static void main(String[] args){
        SpringApplication.run(ServiceOssApplication.class, args);
    }
}
