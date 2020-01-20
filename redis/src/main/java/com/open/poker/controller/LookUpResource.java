package com.open.poker.controller;

import com.open.poker.repository.UserProfileRepository;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@Flogger
@RequestMapping(path = "/check")
public class LookUpResource {

    @Autowired
    private UserProfileRepository repository;

    @GetMapping(path = "/username/{name}")
    public ResponseEntity<Boolean> checkUserName(@PathVariable("name") @NotBlank @Size(min = 4, max = 20) String username) {
        log.atInfo().log("Checking if username %s is available", username);
        var response = repository.findByUsernameOrEmail(username).isPresent();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable("email") @NotBlank @Email @Size(min = 1, max = 40) String email) {
        log.atInfo().log("Checking if email %s is available", email);
        var response = repository.findByUsernameOrEmail(email).isPresent();
        return ResponseEntity.ok(response);
    }
}
