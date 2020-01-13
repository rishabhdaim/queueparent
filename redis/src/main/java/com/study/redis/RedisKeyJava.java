package com.study.redis;

import com.study.redis.util.ClientUtil;

import java.util.Set;

public class RedisKeyJava {
    public static void main(String[] args) {

        //Connecting to Redis server on localhost
        var jedis = ClientUtil.getJedis();
        System.out.println("Connection to server successfully");
        //store data in redis list
        // Get the stored data and print it
        Set<String> list = jedis.keys("*");

        list.forEach(System.out::println);
    }
}
