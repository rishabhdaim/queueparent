package com.study.redis.vote.schemas;

import com.study.redis.constants.RedisConstants;

public enum OrderBy {

    TIME(RedisConstants.TIME_SET), SCORE(RedisConstants.SCORE_SET);

    private final String name;
    OrderBy(final String name) {
        this.name = name;
    }

    public String getKey() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
