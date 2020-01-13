package com.study.redis.constants;

public class RedisConstants {

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String PROP_FILE = "redis";
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;


    // Article Votes

    public static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    public static final int ARTICLES_PER_PAGE = 25;
    public static final int VOTE_SCORE = 432;
    public static final String TIME_SET = "time:";
    public static final String SCORE_SET = "score:";
    public static final String USER_NAME_SET = "user_name:";
    public static final String EMAIL_SET = "user_email:";

    public static final String ARTICLE_KEY = "article:";
    public static final String USER_PROFILE_KEY = "up:";
    public static final String USER_EMAIL_KEY = "email:";
    public static final String USER_KEY = "user:";
    public static final String VOTED_KEY = "voted:";
    public static final String UP_VOTED_KEY = "up_voted:";
    public static final String DOWN_VOTED_KEY = "down_voted:";
    public static final String GROUP_KEY = "group:";

    public static final String VOTES_KEY = "votes";
    public static final String SCORES_KEY = "score";
    public static final String TITLE_KEY = "title";
    public static final String LINK_KEY = "link";
    public static final String POSTER_KEY = "poster";
    public static final String TIME_KEY = "time";
    public static final String ID_KEY = "id";
    public static final String USER_NAME_KEY = "username";
    public static final String FIRST_NAME_KEY = "firstname";
    public static final String LAST_NAME_KEY = "lastname";
    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";

    // E tailor constants
    public static final String LOGIN_KEY = "login:";

    // Error Constants
    public static final String UN_AUTHORIZED = "Unauthorized";
    public static final String BEARER = "Bearer";

}
