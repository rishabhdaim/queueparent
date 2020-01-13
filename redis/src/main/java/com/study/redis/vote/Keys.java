package com.study.redis.vote;


import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class Keys {

    public String toKey(final long id, final Supplier<String> prefix) {
        return prefix.get()+id;
    }

    public String toKey(final String id, final Supplier<String> prefix) {
        return prefix.get()+id;
    }
}
