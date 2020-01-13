package com.study.redis.vote;

import com.google.common.base.Preconditions;
import com.study.redis.constants.RedisConstants;
import com.study.redis.vote.schemas.*;
import lombok.experimental.UtilityClass;
import lombok.extern.flogger.Flogger;
import org.mindrot.jbcrypt.BCrypt;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.flogger.LazyArgs.lazy;
import static com.study.redis.constants.RedisConstants.*;

@Flogger
@UtilityClass
public final class VoteUtils {

    public static List<Article> getGroupArticles(final Jedis jedis, final String groupName, final int page, final OrderBy orderBy) {

        final String groupKey = Keys.toKey(groupName, orderBy::getKey);

        if (!jedis.exists(groupKey)) {
            ZParams zParams = new ZParams();
            zParams.aggregate(ZParams.Aggregate.MAX);
            jedis.zinterstore(groupKey, zParams, Keys.toKey(groupName, () -> GROUP_KEY), orderBy.getKey());
            jedis.expire(groupKey, 60);
        }

        return getArticles(jedis, page, orderBy);
    }

    public static void addRemoveGroups(final Jedis jedis, final long articleId, final long[] toAdd, final long[] toRemove) {
        final String articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);

        for (long id: toAdd) {
            jedis.sadd(Keys.toKey(id, () -> GROUP_KEY), articleKey);
        }

        for (long id: toRemove) {
            jedis.srem(Keys.toKey(id, () -> GROUP_KEY), articleKey);
        }
    }

    public static boolean voteArticle(final Jedis jedis, final User user, final long articleId, final Vote vote) {
        if (hasTimedOut(jedis, user, articleId, "Voting skipped for Article %s from user %s because of time cutoff")) {
            return false;
        }
        return voteArticle( al -> al == 1L, jedis, articleId, user, vote);
    }

    public static User createUser(final Jedis jedis) {
        var user = User.of(jedis.incr(RedisConstants.USER_KEY));
        log.atInfo().log("Created new user with id %s", user.getId());
        return user;
    }

    public static boolean signUpUser(final Jedis jedis, final SignUpForm signUpForm) {
        var userProfileKey = Keys.toKey(signUpForm.getUsername(), () -> USER_PROFILE_KEY);
        var userEmailKey = Keys.toKey(signUpForm.getEmail(), () -> USER_EMAIL_KEY);
        return signUpUser(al -> al, jedis, userProfileKey, userEmailKey, signUpForm);
    }

    public Optional<UserProfile> getUserProfileByName(final Jedis jedis, final String username) {
        var userProfileKey = Keys.toKey(username, () -> USER_PROFILE_KEY);
        return UserProfile.ofMap(username, jedis.hgetAll(userProfileKey));
    }

    public Optional<UserProfile> getUserProfileByEmail(final Jedis jedis, final String email) {
        var userEmailKey = Keys.toKey(email, () -> USER_EMAIL_KEY);
        return UserProfile.ofMap(email, jedis.hgetAll(userEmailKey));
    }

    public Optional<UserProfile> getUserProfile(final Jedis jedis, final String userNameOrEmail) {
        return getUserProfileByName(jedis, userNameOrEmail).or(() -> getUserProfileByEmail(jedis, userNameOrEmail));
    }

    public static boolean checkUsername(final Jedis jedis, final String username) {
        return jedis.sismember(USER_NAME_SET, username);
    }

    public static boolean checkEmail(final Jedis jedis, final String email) {
        return jedis.sismember(EMAIL_SET, email);
    }

    private static boolean signUpUser(final Predicate<Boolean> predicate, final Jedis jedis, final String userProfileKey, final String userEmailKey, final SignUpForm signUpForm) {
        if (!predicate.test(checkEmail(jedis, signUpForm.getEmail()) || checkUsername(jedis, signUpForm.getUsername()))) {

            jedis.sadd(USER_NAME_SET, signUpForm.getUsername());
            jedis.sadd(EMAIL_SET, signUpForm.getEmail());

            jedis.hmset(userProfileKey, new HashMap<>() {{
                put(ID_KEY, String.valueOf(signUpForm.getId()));
                put(USER_NAME_KEY, signUpForm.getUsername());
                put(EMAIL_KEY, signUpForm.getEmail());
                put(PASSWORD_KEY, hashPwd(signUpForm.getPassword()));
                put(FIRST_NAME_KEY, Optional.ofNullable(signUpForm.getFirstName()).orElse(signUpForm.getUsername()));
                put(LAST_NAME_KEY, Optional.ofNullable(signUpForm.getLastName()).orElse(signUpForm.getUsername()));
            }});

            jedis.hmset(userEmailKey, new HashMap<>() {{
                put(ID_KEY, String.valueOf(signUpForm.getId()));
                put(USER_NAME_KEY, signUpForm.getUsername());
                put(EMAIL_KEY, signUpForm.getEmail());
                put(PASSWORD_KEY, hashPwd(signUpForm.getPassword()));
                put(FIRST_NAME_KEY, Optional.ofNullable(signUpForm.getFirstName()).orElse(signUpForm.getUsername()));
                put(LAST_NAME_KEY, Optional.ofNullable(signUpForm.getLastName()).orElse(signUpForm.getUsername()));
            }});
            log.atInfo().log("User Profile for user %s with name %s has been created", signUpForm.getId(), signUpForm.getUsername());
            return true;
        }
        log.atWarning().log("User Profile for user %s with name %s already exists", signUpForm.getId(), signUpForm.getUsername());
        return false;
    }

    public static boolean loginUser(final Jedis jedis, final LoginForm loginForm) {
        var userProfileKey = Keys.toKey(loginForm.getUsernameOrEmail(), () -> USER_PROFILE_KEY);
        final var hashPwd = Optional.ofNullable(jedis.hget(userProfileKey, PASSWORD_KEY))
                .or(() -> Optional.ofNullable(jedis.hget(Keys.toKey(loginForm.getUsernameOrEmail(), () -> USER_EMAIL_KEY), PASSWORD_KEY)));
        var b = hashPwd.isPresent() && matchPwd(loginForm.getPassword(), hashPwd.get());
        log.atInfo().log("User %s Logged-in %s", loginForm.getUsernameOrEmail(), b ? lazy(() -> "Successfully"): lazy(() -> "failed"));
        return b;
    }

    private String hashPwd(final String pwd) {
        return BCrypt.hashpw(pwd, BCrypt.gensalt(10));
    }

    private boolean matchPwd(final String pwd, final String hashPwd) {
        return BCrypt.checkpw(pwd, hashPwd);
    }

    public static long postArticle(final Jedis jedis, final User user, final ArticleData articleData) {

        var articleId = jedis.incr(ARTICLE_KEY);

        var articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);
        var votedKey = Keys.toKey(articleId, () -> VOTED_KEY);

        // create voted set for new article
        jedis.sadd(votedKey, user.toKey());
        jedis.expire(votedKey, ONE_WEEK_IN_SECONDS);

        var now = Instant.now().getEpochSecond();
        final var score = Vote.UP_VOTE.score(VOTE_SCORE);

        jedis.hmset(articleKey, new HashMap<>() {{
            put(TITLE_KEY, articleData.getTitle());
            put(LINK_KEY, articleData.getLink());
            put(POSTER_KEY, user.toKey());
            put(TIME_KEY, String.valueOf(now));
            put(VOTES_KEY, String.valueOf(1));
            put(SCORES_KEY, String.valueOf(now + score));
        }});

        var scoreSet = jedis.zadd(SCORE_SET, now + score, articleKey);
        var timeSet = jedis.zadd(TIME_SET, now, articleKey);

        log.atInfo().log("Article %s posted by user %s. Score set %s, Time Set %s", articleKey, user.toKey(), scoreSet, timeSet);

        return articleId;
    }

    public static boolean deleteArticle(final Jedis jedis, final User user, final long articleId) {

        var articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);
        var votedKey = Keys.toKey(articleId, () -> VOTED_KEY);

        // delete article from Map if posted by same User
        if (Objects.equals(jedis.hget(articleKey, POSTER_KEY), user.toKey())) {
            jedis.hdel(articleKey, TITLE_KEY, LINK_KEY, POSTER_KEY, TIME_KEY, VOTES_KEY, SCORES_KEY);
            jedis.srem(votedKey, user.toKey());
            jedis.zrem(SCORE_SET, articleKey);
            jedis.zrem(TIME_SET, articleKey);
            log.atInfo().log("Article %s posted by user %s has been deleted", articleKey, user.toKey());
            return true;
        }
        log.atSevere().log("Unable to delete article %s", articleId);
        return false;
    }

    public static List<Article> getArticles(final Jedis jedis, final int page, final OrderBy orderBy) {
        Preconditions.checkArgument(page > 0);
        var start = (page - 1) * ARTICLES_PER_PAGE;
        var end = start + ARTICLES_PER_PAGE - 1;
        var ids = jedis.zrevrange(orderBy.getKey(), start, end);
        return ids.stream().map(id -> Article.ofMap(id, jedis.hgetAll(id))).collect(Collectors.toUnmodifiableList());
    }

    public static Article getArticle(final Jedis jedis, final long articleId) {
        var articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);
        return Article.ofMap(articleKey, jedis.hgetAll(articleKey));
    }

    /**
     * Add Vote for the article for the user (if npt already voted)
     *
     * @param predicate check whether users has already voted or not
     * @param jedis {@link Jedis client}
     * @param articleId {@link Article to vote}
     * @param user {@link User voting user}
     */
    private static boolean voteArticle(final Predicate<Long> predicate, final Jedis jedis, final long articleId, final User user, final Vote vote) {
        if (predicate.test(jedis.sadd(vote.toKey(articleId), user.toKey()))) {
            var articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);
            jedis.zincrby(SCORE_SET, vote.score(VOTE_SCORE), articleKey);
            jedis.hincrBy(articleKey, VOTES_KEY, 1);
            jedis.hincrBy(articleKey, SCORES_KEY, vote.score(VOTE_SCORE));
            log.atInfo().log("Article %s voted by user %s", articleKey, user.toKey());
            return true;
        }
        log.atWarning().log("Voting skipped for Article %s from user %s because of already voted", articleId, user.toKey());
        return false;
    }

    private static boolean changeVote(final Predicate<Long> predicate, final Jedis jedis, final long articleId, final User user, final Vote srcVote, final Vote dstVote) {
        if (predicate.test(jedis.smove(srcVote.toKey(articleId), dstVote.toKey(articleId), user.toKey()))) {
            var articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);
            jedis.zincrby(SCORE_SET, dstVote.score(2 * VOTE_SCORE), articleKey);
            jedis.hincrBy(articleKey, SCORES_KEY, dstVote.score(2 * VOTE_SCORE));
            log.atInfo().log("Vote for Article %s has been changed for user %s", articleKey, user.toKey());
            return true;
        }
        log.atWarning().log("Change Vote skipped for Article %s from user %s", articleId, user.toKey());
        return false;
    }

    public static boolean changeVote(final Jedis jedis, final User user, final long articleId) {

        if (hasTimedOut(jedis, user, articleId, "Change Vote skipped for Article %s from user %s because of time cutoff")) {
            return false;
        }
        if (jedis.sismember(Vote.UP_VOTE.toKey(articleId), user.toKey())) {
            return changeVote(al -> al == 1, jedis, articleId, user, Vote.UP_VOTE, Vote.DOWN_VOTE);
        }
        return changeVote(al -> al == 1, jedis, articleId, user, Vote.DOWN_VOTE, Vote.UP_VOTE);
    }

    private static boolean hasTimedOut(final Jedis jedis, final User user, final long articleId, final String msg) {
        var cutOff = Instant.now().getEpochSecond() - ONE_WEEK_IN_SECONDS;

        var articleKey = Keys.toKey(articleId, () -> ARTICLE_KEY);

        if (jedis.zscore(TIME_SET, articleKey) < cutOff) {
            log.atWarning().log(msg, articleId, user.toKey());
            return true;
        }
        return false;
    }
}
