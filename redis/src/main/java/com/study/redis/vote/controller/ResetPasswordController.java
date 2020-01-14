package com.study.redis.vote.controller;

import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.validation.constraints.Email;

@RestController
@Flogger
@RequestMapping("/reset")
public class ResetPasswordController {

    @Autowired
    private Jedis jedis;

    @PostMapping(path = "/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createArticle(@PathVariable("email") @Email String emailId) {
        log.atInfo().log("Resetting Password for %s", emailId);
        return ResponseEntity.ok("");
    }
}
