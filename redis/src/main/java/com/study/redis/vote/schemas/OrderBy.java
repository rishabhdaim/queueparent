package com.study.redis.vote.schemas;

import static com.study.redis.constants.RedisConstants.*;

public enum OrderBy {

    TIME(TIME_KEY, TIME_SET), SCORE(SCORES_KEY, SCORE_SET);

    private final String name;
    private final String key;

    OrderBy(final String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return name;
    }
}
