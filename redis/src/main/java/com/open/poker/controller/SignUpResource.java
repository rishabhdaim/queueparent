package com.open.poker.controller;

import com.open.poker.model.UserProfile;
import com.open.poker.repository.UserProfileRepository;
import com.open.poker.schema.SignupRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.vavr.control.Try;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@Flogger
@RequestMapping(path = "/signup")
@Api(value = "Signing Up User to System", consumes = "application/json")
public class SignUpResource {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PostMapping()
    @ApiOperation(value = "Sign up a new user", response = String.class)
    public ResponseEntity<?> signUpUser(@ApiParam(value = "Signing-in object of new User to store database", required = true) @Valid @RequestBody SignupRequest request) {
        log.atInfo().log("Signing Up new User %s with Email %s", request.getUsername(), request.getEmail());

        if (userProfileRepository.findByUsernameOrEmail(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (userProfileRepository.findByUsernameOrEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        var response = Try.of(() -> userProfileRepository.save(UserProfile.of(request)))
                .orElse(Try.failure(new RuntimeException("Unable to save user : " + request.getUsername())));

        if (response.isSuccess()) {
            var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/signup/{id}").buildAndExpand(response.get().getId()).toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save user. Try Again");
        }
    }
}
