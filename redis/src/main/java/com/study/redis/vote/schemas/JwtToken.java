package com.study.redis.vote.schemas;

import lombok.NonNull;
import lombok.Value;

@Value
public class JwtToken {
    @NonNull private final String token;
}
