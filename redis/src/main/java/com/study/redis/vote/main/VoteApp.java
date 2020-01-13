package com.study.redis.vote.main;

import com.study.redis.constants.RedisConstants;
import com.study.redis.vote.schemas.ArticleData;
import com.study.redis.vote.schemas.User;
import com.study.redis.vote.schemas.Vote;

import static com.study.redis.util.ClientUtil.getJedis;
import static com.study.redis.vote.VoteUtils.*;

public class VoteApp {

    public static void main(String[] args) {

        final var jedis = getJedis();
        System.out.println("Connection to server successfully");
        System.out.println(jedis.ping());

        final var user = User.of(jedis.incr(RedisConstants.USER_KEY));
        final var articleData = new ArticleData("Go to Statement is bad", "https://www.google.com");

        final var articleId = postArticle(jedis, user, articleData);
        System.out.println(articleId);
        System.out.println(user);

        final var article = getArticle(jedis, articleId);
        System.out.println(article);

        voteArticle(jedis, User.of(jedis.incr(RedisConstants.USER_KEY)), articleId, Vote.UP_VOTE);
        voteArticle(jedis, User.of(jedis.incr(RedisConstants.USER_KEY)), articleId, Vote.UP_VOTE);
        voteArticle(jedis, User.of(jedis.incr(RedisConstants.USER_KEY)), articleId, Vote.UP_VOTE);
        voteArticle(jedis, User.of(jedis.incr(RedisConstants.USER_KEY)), articleId, Vote.UP_VOTE);
        voteArticle(jedis, User.of(jedis.incr(RedisConstants.USER_KEY)), articleId, Vote.UP_VOTE);

        var newUser = User.of(jedis.incr(RedisConstants.USER_KEY));
        voteArticle(jedis, newUser, articleId, Vote.UP_VOTE);
        System.out.println(changeVote(jedis, newUser, articleId));
    }
}
