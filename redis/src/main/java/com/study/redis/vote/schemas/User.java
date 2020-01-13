package com.study.redis.vote.schemas;


import com.google.common.base.Preconditions;
import com.google.common.flogger.LazyArgs;
import lombok.Value;
import lombok.extern.flogger.Flogger;

@Value
@Flogger
public class User {

    private final long id;

    private User(long id) {
        this.id = id;
    }

    public static User of(long id) {
        Preconditions.checkArgument(id > 0);
        return new User(id);
    }

    public String toKey() {
        log.atInfo().log("Getting User Info for %s", LazyArgs.lazy(this::getId));
        return "user:"+id;
    }
}
