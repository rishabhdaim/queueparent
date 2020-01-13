package com.study.redis.vote.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.study.redis"})
public class SpringVotingMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringVotingMain.class, args);
    }
}
