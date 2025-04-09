package com.bit.srb.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloomFilterService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BLOOM_FILTER_KEY = "bloom:lend";

    // 初始化布隆过滤器
    public void initializeBloomFilter(List<Long> lendIds) {
        for (Long lendId : lendIds) {
            redisTemplate.opsForValue().setBit(BLOOM_FILTER_KEY, lendId, true);
        }
    }

    // 检查标的 ID 是否存在
    public boolean mightContain(Long lendId) {
        return redisTemplate.opsForValue().getBit(BLOOM_FILTER_KEY, lendId);
    }
}
