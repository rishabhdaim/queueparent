package com.study.redis.vote.controller;

import com.study.redis.vote.beans.RedisConfiguration;
import com.study.redis.vote.exception.LoginExceptionController;
import com.study.redis.vote.jwt.JwtAuthenticationEntryPoint;
import com.study.redis.vote.jwt.JwtRequestFilter;
import com.study.redis.vote.jwt.JwtTokenUtil;
import com.study.redis.vote.jwt.JwtUserDetailsService;
import com.study.redis.vote.schemas.JwtTokenResponse;
import com.study.redis.vote.schemas.LoginRequest;
import com.study.redis.vote.schemas.SignUpRequest;
import com.study.redis.vote.security.WebSecurityConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SignUpController.class, LoginController.class, RedisConfiguration.class, WebSecurityConfig.class,
        JwtAuthenticationEntryPoint.class, JwtRequestFilter.class, JwtUserDetailsService.class, JwtTokenUtil.class, LoginExceptionController.class})
public class SignUpAndLoginControllerTest {

    @Autowired
    private SignUpController signUpController;

    @Autowired
    private LoginController loginController;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Jedis jedis;

    private final String password = "password";
    @Test
    public void validateSignUpPositive() {
        SignUpRequest signUpRequest = new SignUpRequest(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpRequest);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        LoginRequest loginRequest = new LoginRequest("daim", password);
        entity = loginController.loginUser(loginRequest);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        assertTrue(entity.getBody() instanceof JwtTokenResponse);
        assertEquals(jwtTokenUtil.getUsernameFromToken(((JwtTokenResponse)entity.getBody()).getToken()), "daim");

        loginRequest = new LoginRequest("rishabhdaim1991@gmail.com", password);
        entity = loginController.loginUser(loginRequest);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        assertTrue(entity.getBody() instanceof JwtTokenResponse);
        assertEquals(jwtTokenUtil.getUsernameFromToken(((JwtTokenResponse)entity.getBody()).getToken()), "daim");
    }

    @Test(expected = BadCredentialsException.class)
    public void validateSignUpWrongUserName() {
        SignUpRequest signUpRequest = new SignUpRequest(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpRequest);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        var loginForm = new LoginRequest("daim1", password);
        loginController.loginUser(loginForm);
    }

    @Test(expected = BadCredentialsException.class)
    public void validateSignUpWrongUserEmail() {
        SignUpRequest signUpRequest = new SignUpRequest(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpRequest);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        var loginForm = new LoginRequest("rishabhdaim1991@gmail.comm", password);
        loginController.loginUser(loginForm);
    }

    @Test(expected = BadCredentialsException.class)
    public void validateSignUpWrongPassword() {
        SignUpRequest signUpRequest = new SignUpRequest(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpRequest);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        var loginForm = new LoginRequest("daim", "password1");
        loginController.loginUser(loginForm);
    }

    @Before
    public void setUp() {
        jedis.flushAll();
    }

    @After
    public void tearDown() {
        jedis.flushAll();
    }
}
