package com.study.redis.util;

import com.study.redis.constants.RedisConstants;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class PropertiesUtil {

    private PropertiesUtil() throws Exception {
        throw new Exception("No Instances for you");
    }

    private static Properties prop;

    public static Properties getProps()  {
        checkForNull();
        return prop;
    }

    private synchronized static void checkForNull() {
        if (Objects.isNull(prop)) {
            prop = loadProp();
        }
    }

    public static String getHost() {
        checkForNull();
        return prop.getProperty(RedisConstants.HOST);
    }

    private static Properties loadProp() {
        var prop = new Properties();
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(RedisConstants.PROP_FILE)) {
            assert input != null;
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
}
