package com.study.redis.vote.schemas;

import com.study.redis.constants.RedisConstants;
import com.study.redis.vote.Keys;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public enum Vote {
    UP_VOTE (i -> i, () -> RedisConstants.UP_VOTED_KEY) , DOWN_VOTE(i -> -i, () -> RedisConstants.DOWN_VOTED_KEY);

    private final IntFunction<Integer> calScore;
    private final Supplier<String> keySupplier;

    public int score(final int score) {
        return calScore.apply(score);
    }

    public String toKey(final long articleId) {
        return Keys.toKey(articleId, keySupplier);
    }

    Vote(final IntFunction<Integer> calScore, final Supplier<String> keySupplier) {
        this.calScore = calScore;
        this.keySupplier = keySupplier;
    }
}
