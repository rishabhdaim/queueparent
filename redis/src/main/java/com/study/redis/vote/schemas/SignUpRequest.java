package com.study.redis.vote.schemas;

import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Value()
public class SignUpRequest {
    @NonNull @Min(1) private final long id;
    @NonNull @NotEmpty(message = "Please provide username") @Size(min = 4, max = 16) private final String username;
    @NonNull @Email @NotEmpty(message = "Please provide email") @Size(max = 64) private final String email;
    @NonNull @Size(min = 8, max = 20) @NotEmpty(message = "Please provide password")private final String password;
    private final String firstName;
    private final String lastName;
}
