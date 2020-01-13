package com.study.redis;

import static com.study.redis.util.ClientUtil.getJedis;

public class RedisFlush {
    public static void main(String[] args) {
        var jedis = getJedis();
        System.out.println(jedis.flushAll());
    }
}
