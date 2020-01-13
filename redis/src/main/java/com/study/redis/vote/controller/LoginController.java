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
    public ResponseEntity<JwtToken> loginUser(@RequestBody LoginForm loginForm) {
        log.atInfo().log("User %s trying to login", loginForm.getUsernameOrEmail());

        final Optional<UserProfile> userProfile = VoteUtils.getUserProfile(jedis, loginForm.getUsernameOrEmail());
        final String username = userProfile.orElse(UserProfile.EMPTY).getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginForm.getPassword()));
        return ResponseEntity.ok(new JwtToken(jwtTokenUtil.generateToken(loginForm.withUsernameOrEmail(username))));
    }
}
