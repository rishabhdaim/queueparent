package com.open.poker.controller;

import com.open.poker.jwt.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@Flogger
@RequestMapping("/authenticate}")
public class AuthenticateResource {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/{username}")
    @ApiOperation(value = "Sign up a new user", response = String.class)
    public ResponseEntity<Boolean> authenticate(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization, @PathVariable("username") @NotBlank String username) {

        log.atInfo().log("Authenticating User %s", username);

       if (jwtTokenUtil.validateToken(authorization, username)) {
           return ResponseEntity.ok(true);
       } else {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
       }
    }
}
