package com.study.redis.vote.schemas;

import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value()
public class LoginForm {
    @NonNull @With private final String usernameOrEmail;
    @NonNull private final String password;
}
