package com.study.redis.etailor;

import com.study.redis.constants.RedisConstants;
import com.study.redis.etailor.schema.Token;
import lombok.experimental.UtilityClass;
import lombok.extern.flogger.Flogger;
import redis.clients.jedis.Jedis;

@UtilityClass
@Flogger
public class EtailorUtils {

    public String checkToken(final Jedis jedis, final Token token) {
        return jedis.hget(RedisConstants.LOGIN_KEY, token.getName());
    }
}
