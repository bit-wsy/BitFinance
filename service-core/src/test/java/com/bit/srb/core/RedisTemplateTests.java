package com.bit.srb.core;


import com.bit.srb.core.mapper.DictMapper;
import com.bit.srb.core.pojo.entity.Dict;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
//@RunWith(SpringRunner.class)该注解已经被优化
public class RedisTemplateTests {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DictMapper dictMapper;

    @Test
    public void saveDict(){
        Dict dict = dictMapper.selectById(1);
        redisTemplate.opsForValue().set("dict",dict,5, TimeUnit.MINUTES);
        // forvalue 存字符串（序列化方式）
    }

    @Test
    public void getDict(){
        Dict dict = (Dict) redisTemplate.opsForValue().get("dict");
        System.out.println(dict);
    }
}
