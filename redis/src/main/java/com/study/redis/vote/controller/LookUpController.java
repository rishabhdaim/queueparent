package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@RestController
@Flogger
@RequestMapping(path = "/check")
public class LookUpController {

    @Autowired
    private Jedis jedis;

    @PostMapping(path = "/username/{name}")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkUserName(@PathVariable("name") @NotEmpty String username) {
        log.atInfo().log("Checking username %s is available", username);
        return VoteUtils.checkUsername(jedis, username);
    }

    @PostMapping(path = "/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkEmail(@PathVariable("email") @NotEmpty @Email String email) {
        log.atInfo().log("Checking email %s is available", email);
        return VoteUtils.checkEmail(jedis, email);
    }
}
