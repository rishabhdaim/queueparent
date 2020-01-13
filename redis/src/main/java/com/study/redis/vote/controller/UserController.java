package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.User;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
@Flogger
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private Jedis jedis;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser() {
        log.atInfo().log("Creating new User");
        return VoteUtils.createUser(jedis);
    }
}
