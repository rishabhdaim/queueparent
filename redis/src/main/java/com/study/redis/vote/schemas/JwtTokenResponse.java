package com.study.redis.vote.schemas;

import lombok.NonNull;
import lombok.Value;

@Value
public class JwtTokenResponse {
    @NonNull private final String token;
    @NonNull private final String tokenType = "Bearer";
}
