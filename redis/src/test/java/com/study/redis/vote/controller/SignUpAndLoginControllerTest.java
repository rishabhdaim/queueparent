package com.study.redis.vote.controller;

import com.study.redis.vote.beans.RedisBean;
import com.study.redis.vote.exception.LoginExceptionController;
import com.study.redis.vote.jwt.JwtAuthenticationEntryPoint;
import com.study.redis.vote.jwt.JwtRequestFilter;
import com.study.redis.vote.jwt.JwtTokenUtil;
import com.study.redis.vote.jwt.JwtUserDetailsService;
import com.study.redis.vote.schemas.JwtToken;
import com.study.redis.vote.schemas.LoginForm;
import com.study.redis.vote.schemas.SignUpForm;
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
@ContextConfiguration(classes = { SignUpController.class, LoginController.class, RedisBean.class, WebSecurityConfig.class,
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
        SignUpForm signUpForm = new SignUpForm(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpForm);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        LoginForm loginForm = new LoginForm("daim", password);
        entity = loginController.loginUser(loginForm);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        assertTrue(entity.getBody() instanceof JwtToken);
        assertEquals(jwtTokenUtil.getUsernameFromToken(((JwtToken)entity.getBody()).getToken()), "daim");

        loginForm = new LoginForm("rishabhdaim1991@gmail.com", password);
        entity = loginController.loginUser(loginForm);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);
        assertTrue(entity.getBody() instanceof JwtToken);
        assertEquals(jwtTokenUtil.getUsernameFromToken(((JwtToken)entity.getBody()).getToken()), "daim");
    }

    @Test(expected = BadCredentialsException.class)
    public void validateSignUpWrongUserName() {
        SignUpForm signUpForm = new SignUpForm(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpForm);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        var loginForm = new LoginForm("daim1", password);
        loginController.loginUser(loginForm);
    }

    @Test(expected = BadCredentialsException.class)
    public void validateSignUpWrongUserEmail() {
        SignUpForm signUpForm = new SignUpForm(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpForm);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        var loginForm = new LoginForm("rishabhdaim1991@gmail.comm", password);
        loginController.loginUser(loginForm);
    }

    @Test(expected = BadCredentialsException.class)
    public void validateSignUpWrongPassword() {
        SignUpForm signUpForm = new SignUpForm(1, "daim", "rishabhdaim1991@gmail.com", password, "", "");
        var entity = signUpController.signUpUser(signUpForm);
        assertEquals(entity.getStatusCode(), HttpStatus.OK);

        var loginForm = new LoginForm("daim", "password1");
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
