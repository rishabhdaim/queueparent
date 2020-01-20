package com.open.poker.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;
import lombok.Value;

@Value
public class TokenResponse {
    @NonNull @JsonIgnore private final String token;
    @NonNull private final String tokenType = "Bearer";
}
