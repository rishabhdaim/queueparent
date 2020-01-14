package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.SignUpRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "Sign Up System", consumes = "application/json")
public class SignUpController {

    @Autowired
    private Jedis jedis;

    @PostMapping()
    @ApiOperation(value = "Sign up a new user", response = String.class)
    public ResponseEntity<?> signUpUser(@ApiParam(value = "Signing-in object of new User to store database", required = true) @Valid @RequestBody SignUpRequest signUpRequest) {
        log.atInfo().log("Signing Up new User %s with Email %s", signUpRequest.getUsername(), signUpRequest.getEmail());

        if (VoteUtils.checkUsername(jedis, signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (VoteUtils.checkEmail(jedis, signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        if (VoteUtils.signUpUser(jedis, signUpRequest)) {
            return ResponseEntity.ok(signUpRequest.getId());
        } else  {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some Error");
        }
    }
}
