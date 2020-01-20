package com.open.poker.schema;

import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.*;

@Value()
public class SignupRequest {
    @NonNull @NotBlank(message = "Please provide a username") @Size(min = 4, max = 20) private final String username;
    @NonNull @Email @NotBlank(message = "Please provide an email") @Size(min = 1, max = 40) private final String email;
    @NonNull @Size(min = 8, max = 20) @NotBlank(message = "Please provide a password")private final String password;
    private final String firstName;
    private final String lastName;
}
