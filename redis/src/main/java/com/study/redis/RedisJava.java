package com.study.redis;

import com.study.redis.util.ClientUtil;
import com.study.redis.util.PropertiesUtil;
import redis.clients.jedis.Jedis;

public class RedisJava {
    public static void main(String[] args) {
        var jedis = ClientUtil.getClient();
        System.out.println("Connection to server successfully");
        System.out.println(jedis.ping());
    }
}
