package com.bit.srb.core.service;

import com.bit.srb.core.mapper.LendMapper;
import com.bit.srb.core.pojo.entity.Lend;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class LendCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LendMapper lendMapper;

    @Autowired
    private BloomFilterService bloomFilterService;

    private static final String LEND_CACHE_KEY_PREFIX = "lend:";
    private static final String HOTDATA_ACCESS_KEY = "hotdata:access";
    private static final ExecutorService CACHE_REFRESH_EXECUTOR = Executors.newSingleThreadExecutor();

    // 更新访问次数
    public void incrementAccessCount(Long lendId) {
        redisTemplate.opsForZSet().incrementScore(HOTDATA_ACCESS_KEY, lendId, 1);
    }

    // 判断是否为热点数据
    public boolean isHotData(Long lendId, int topN) {
        Set<Object> topNIds = redisTemplate.opsForZSet().reverseRange(HOTDATA_ACCESS_KEY, 0, topN - 1);
        return topNIds != null && topNIds.contains(lendId);
    }

    // 获取标的数据
    public Lend getLend(Long lendId) {
        // 1. 检查布隆过滤器
        if (!bloomFilterService.mightContain(lendId)) {
            throw new IllegalArgumentException("标的不存在");
        }

        // 2. 更新访问次数
        incrementAccessCount(lendId);

        // 3. 判断是否为热点数据
        boolean isHot = isHotData(lendId, 10); // 前 10 名为热点数据

        if (isHot) {
            // 热点数据：使用逻辑过期缓存
            return getLendWithLogicalExpire(lendId);
        } else {
            // 非热点数据：使用普通缓存逻辑
            return getLendWithRandomExpire(lendId);
        }
    }

    // 逻辑过期缓存逻辑
    public Lend getLendWithLogicalExpire(Long lendId) {
        String cacheKey = LEND_CACHE_KEY_PREFIX + lendId;

        // 从 Redis 中获取数据
        LendCacheWrapper cacheWrapper = (LendCacheWrapper) redisTemplate.opsForValue().get(cacheKey);
        if (cacheWrapper != null) {
            if (cacheWrapper.getExpireTime() > System.currentTimeMillis()) {
                // 缓存未过期，直接返回
                return cacheWrapper.getLend();
            } else {
                // 缓存已过期，异步刷新
                CACHE_REFRESH_EXECUTOR.submit(() -> {
                    Lend lend = lendMapper.selectById(lendId);
                    cacheWrapper.setLend(lend);
                    cacheWrapper.setExpireTime(System.currentTimeMillis() + 60 * 60 * 1000); // 1小时
                    redisTemplate.opsForValue().set(cacheKey, cacheWrapper);
                });
                return cacheWrapper.getLend(); // 返回旧数据
            }
        }

        // 如果 Redis 中没有数据，从数据库中读取
        Lend lend = lendMapper.selectById(lendId);
        if (lend == null) {
            throw new IllegalArgumentException("标的不存在");
        }

        // 写入 Redis，设置逻辑过期时间
        LendCacheWrapper newCacheWrapper = new LendCacheWrapper();
        newCacheWrapper.setLend(lend);
        newCacheWrapper.setExpireTime(System.currentTimeMillis() + 60 * 60 * 1000); // 1小时
        redisTemplate.opsForValue().set(cacheKey, newCacheWrapper);

        return lend;
    }

    // 普通缓存逻辑
    public Lend getLendWithRandomExpire(Long lendId) {
        String cacheKey = LEND_CACHE_KEY_PREFIX + lendId;

        // 从 Redis 中获取数据
        Lend lend = (Lend) redisTemplate.opsForValue().get(cacheKey);
        if (lend != null) {
            return lend;
        }

        // 如果 Redis 中没有数据，从数据库中读取
        lend = lendMapper.selectById(lendId);
        if (lend == null) {
            throw new IllegalArgumentException("标的不存在");
        }

        // 写入 Redis，并设置随机过期时间
        int ttl = 60 * 60 + (int) (Math.random() * 300); // 随机过期时间：1小时到1小时5分钟
        redisTemplate.opsForValue().set(cacheKey, lend, ttl, TimeUnit.SECONDS);

        return lend;
    }

    // 缓存包装类
    @Setter
    @Getter
    public static class LendCacheWrapper {
        private Lend lend;
        private long expireTime;

    }
}
