package com.open.poker.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;

@Value()
public class LoginRequest {

    @NonNull
    @With
    @JsonIgnore
    @NotBlank(message = "Please provide a username or an email")
    private final String usernameOrEmail;
    @NonNull
    @JsonIgnore
    @NotBlank(message = "Please provide a password")
    private final String password;
}
