package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.jwt.JwtTokenUtil;
import com.study.redis.vote.schemas.*;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@RestController
@Flogger
@RequestMapping(path = "/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Jedis jedis;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JwtTokenResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        log.atInfo().log("User %s trying to login", loginRequest.getUsernameOrEmail());

        final Optional<UserProfile> userProfile = VoteUtils.getUserProfile(jedis, loginRequest.getUsernameOrEmail());
        final String username = userProfile.orElse(UserProfile.EMPTY).getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
        return ResponseEntity.ok(new JwtTokenResponse(jwtTokenUtil.generateToken(loginRequest.withUsernameOrEmail(username))));
    }
}
