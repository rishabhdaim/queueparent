package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.User;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;

@RestController
@Flogger
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private JedisPool jedisPool;

    @PostMapping()
    public ResponseEntity<?> createUser() {
        log.atInfo().log("Creating new User");

        try(Jedis jedis = jedisPool.getResource()) {
            System.out.println(jedis.toString());
            var user = VoteUtils.createUser(jedis);
            var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
