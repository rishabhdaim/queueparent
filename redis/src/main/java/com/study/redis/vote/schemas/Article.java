package com.study.redis.vote.schemas;

import com.google.common.flogger.LazyArgs;
import com.study.redis.constants.RedisConstants;
import com.study.redis.vote.Keys;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.extern.flogger.Flogger;

import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;

@Value(staticConstructor = "of")
@Flogger
public class Article {

    @NonNull private final long id;
    @NonNull @With private final String title;
    @NonNull @With private final String link;
    @NonNull private final String user;
    @NonNull private final long time;
    @NonNull @With private final long votes;
    @NonNull @With private final long score;

    public String toKey() {
        log.at(Level.INFO).log("Getting Key for %s", LazyArgs.lazy(this::getId));
        return Keys.toKey(id, () -> RedisConstants.ARTICLE_KEY);
    }

    public String toVoted() {
        log.atInfo().log("Getting Voted Info for %s", LazyArgs.lazy(this::getId));
        return Keys.toKey(id, () -> RedisConstants.VOTED_KEY);
    }

    public static Article ofMap(final String id, final Map<String, String> value) {
        log.atInfo().log("Creating Article from map for %s", id);
        final var now = Instant.EPOCH.getEpochSecond();
        return value.isEmpty() ? EMPTY_ARTICLE : new Article(Try.of(() -> Long.parseLong(id.split(":")[1])).getOrElse(0L),
                value.getOrDefault(RedisConstants.TITLE_KEY, "NONE"),
                value.getOrDefault(RedisConstants.LINK_KEY, "NONE"),
                value.getOrDefault(RedisConstants.POSTER_KEY, "NONE"),
                Try.of(() -> Long.parseLong(value.getOrDefault(RedisConstants.TIME_KEY, String.valueOf(now)))).getOrElse(now),
                Try.of(() -> Integer.parseInt(value.getOrDefault(RedisConstants.VOTES_KEY, "1"))).getOrElse(1),
                Try.of(() -> Long.parseLong(value.getOrDefault(RedisConstants.SCORES_KEY, String.valueOf(now)))).getOrElse(now));
    }

    private static final Article EMPTY_ARTICLE = Article.of(-1, "", "", "", -1, -1, -1);
}
