package com.study.redis;

import com.study.redis.util.ClientUtil;

public class RedisStringJava {
    public static void main(String[] args) {
        var jedis = ClientUtil.getClient();
        System.out.println("Connection to server sucessfully");
        //set the data in redis string
        jedis.set("tutorial-name", "Redis tutorial");
        // Get the stored data and print it
        System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));
    }
}
