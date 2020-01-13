package com.study.redis;

import com.study.redis.util.ClientUtil;

import java.util.List;

public class RedisListJava {
    public static void main(String[] args) {

        //Connecting to Redis server on localhost
        var jedis = ClientUtil.getJedis();
        System.out.println("Connection to server sucessfully");

        //store data in redis list
        jedis.lpush("tutorial-list", "Redis");
        jedis.lpush("tutorial-list", "Mongodb");
        jedis.lpush("tutorial-list", "Mysql");
        // Get the stored data and print it
        List<String> list = jedis.lrange("tutorial-list", 0 ,5);

        list.forEach(System.out::println);
    }
}
