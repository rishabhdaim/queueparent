package com.study.redis;

import com.study.redis.util.ClientUtil;

public class RedisJava {
    public static void main(String[] args) {
        var jedis = ClientUtil.getJedis();
        System.out.println("Connection to server successfully");
        System.out.println(jedis.ping());
    }
}
