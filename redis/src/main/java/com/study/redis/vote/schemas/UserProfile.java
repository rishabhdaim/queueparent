package com.study.redis.vote.schemas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.redis.constants.RedisConstants;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.extern.flogger.Flogger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Value()
@Flogger
public class UserProfile implements UserDetails {
    @NonNull @With private final String username;
    @NonNull @With @JsonIgnore private final String password;
    @NonNull @With @JsonIgnore private final String email;
    @NonNull @With private final String firstName;
    @NonNull @With private final String lastName;
    @NonNull @With private final Collection<? extends GrantedAuthority> authorities;

    public static Optional<UserProfile> ofMap(final String username, final Map<String, String> value) {
        log.atInfo().log("Creating UserProfile from map for %s", username);
        return value.isEmpty() ? Optional.empty() : Optional.of(new UserProfile(value.getOrDefault(RedisConstants.USER_NAME_KEY, ""),
                value.getOrDefault(RedisConstants.PASSWORD_KEY, ""),
                value.getOrDefault(RedisConstants.EMAIL_KEY, ""),
                value.getOrDefault(RedisConstants.FIRST_NAME_KEY, ""),
                value.getOrDefault(RedisConstants.LAST_NAME_KEY, ""),
                Collections.emptyList()));
    }

    public static final UserProfile EMPTY = new UserProfile("", "", "", "", "", Collections.emptyList());

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
