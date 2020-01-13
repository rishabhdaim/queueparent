package com.study.redis.vote.schemas;

import com.study.redis.constants.RedisConstants;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.extern.flogger.Flogger;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Value()
@Flogger
public class UserProfile {
    @NonNull @With private final String username;
    @NonNull private final String password;
    @NonNull @With private final String email;
    @NonNull @With private final String firstName;
    @NonNull @With private final String lastName;

    public static Optional<UserProfile> ofMap(final String username, final Map<String, String> value) {
        log.atInfo().log("Creating UserProfile from map for %s", username);
        return value.isEmpty() ? Optional.empty() : Optional.of(new UserProfile(value.getOrDefault(RedisConstants.USER_NAME_KEY, ""),
                value.getOrDefault(RedisConstants.PASSWORD_KEY, ""),
                value.getOrDefault(RedisConstants.EMAIL_KEY, ""),
                value.getOrDefault(RedisConstants.FIRST_NAME_KEY, ""),
                value.getOrDefault(RedisConstants.LAST_NAME_KEY, "")));
    }

    public static final UserProfile EMPTY = new UserProfile("", "", "", "", "");
}
