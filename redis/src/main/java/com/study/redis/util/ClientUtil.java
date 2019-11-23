package com.study.redis.util;

import redis.clients.jedis.Jedis;

public final class ClientUtil {

    private static final Jedis INSTANCE = new Jedis(PropertiesUtil.getHost());

    public static Jedis getClient() {
        return INSTANCE;
    }
}
