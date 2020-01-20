package com.open.poker.controller;

import com.open.poker.exception.UserNotFoundException;
import com.open.poker.jwt.JwtTokenUtil;
import com.open.poker.repository.UserProfileRepository;
import com.open.poker.schema.LoginRequest;
import com.open.poker.schema.TokenResponse;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Flogger
@RequestMapping(path = "/login")
public class LoginResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserProfileRepository repository;

    @PostMapping()
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest request) {
        log.atInfo().log("User %s trying to login", request.getUsernameOrEmail());
        var user = repository.findByUsernameOrEmail(request.getUsernameOrEmail()).orElseThrow(() -> new UserNotFoundException("User Not Found with name : " + request.getUsernameOrEmail()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
        log.atInfo().log("User %s is logged-in", user.getUsername());
        return ResponseEntity.ok(new TokenResponse(jwtTokenUtil.generateToken(user.getUsername())));
    }
}
