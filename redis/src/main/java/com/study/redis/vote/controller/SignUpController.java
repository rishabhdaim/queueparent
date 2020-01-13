package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.SignUpForm;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.validation.Valid;

@RestController
@Flogger
@RequestMapping(path = "/signup")
public class SignUpController {

    @Autowired
    private Jedis jedis;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpForm signUpForm) {
        log.atInfo().log("Signing Up new User %s with Email %s", signUpForm.getUsername(), signUpForm.getEmail());

        if (VoteUtils.checkUsername(jedis, signUpForm.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (VoteUtils.checkEmail(jedis, signUpForm.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        if (VoteUtils.signUpUser(jedis, signUpForm)) {
            return ResponseEntity.ok(signUpForm.getId());
        } else  {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
