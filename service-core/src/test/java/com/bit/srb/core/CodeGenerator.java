package com.bit.srb.core;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class CodeGenerator {

        @Test
        public void genCode() {

            FastAutoGenerator.create("jdbc:mysql://localhost:3306/db_srb_core?serverTimezone=GMT%2B8&characterEncoding=utf-8", "root", "wshijd520")
                    .globalConfig(builder -> builder
                            .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                            .enableSwagger()
                            .author("Shell")
                    )
                    .packageConfig(builder -> builder
                            .parent("com.bit.srb.core")
                            .entity("pojo.entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .xml("mapper.xml")
                    )
                    .strategyConfig(builder -> builder
                            .entityBuilder()
                            .enableLombok()
                            .enableRemoveIsPrefix()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(IdType.AUTO)
                            .logicDeleteColumnName("is_deleted")

                    )
                    .strategyConfig(builder -> builder
                            .serviceBuilder()
                            .formatServiceFileName("%sService")

                    )
                    .strategyConfig(builder -> builder
                            .controllerBuilder()
                            .enableRestStyle()

                    )
//                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();



        }
    }

