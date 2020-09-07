/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.springframework.util.StringUtils;

/**
 * 读取config.properties配置工具
 * @author zhongpeiliang
 * @version 0.0.1
 * @since
 */
public class ConfigUtil {
    private static Properties config = new Properties();

    private static long lastReadConfigAt = 0L;

    public static Properties getConfig() {
        long now = System.currentTimeMillis();
        //60秒读一次
        if (0 == config.size() || now - lastReadConfigAt > 60 * 1000L) {
            InputStream resourceAsStream = ConfigUtil.class.getResourceAsStream("/config.properties");
            try {
                InputStreamReader reader = new InputStreamReader(resourceAsStream, "UTF-8");
                config.load(reader);
                resourceAsStream.close();
                reader.close();
                lastReadConfigAt = now;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return config;
    }

    public static String getEnv() {
        getConfig();
        return config.getProperty("env");
    }

    /**
     * 功能描述：
     * 优先取 {env}.{key}对应的值
     * 当{env}.{key}不存在时，直接取{key}对应的值
     */
    public static String getConfigValue(String key) {
        String value = null;
        String env = getEnv();
        String operator = config.getProperty("operator");
        if (StringUtils.hasLength(operator)) {
            value = config.getProperty(operator + "." + key);
        }                
        if (null == value) {
            value = config.getProperty(env + "." + key);
        }
        if (null == value) {
            value = config.getProperty(key);
        }
        return value;
    }

    /**
     * 验证用户借款是否结清 开关值(on:开启,off:关闭)
     * @param key
     * @return
     */
    @Deprecated
    public static String getSwitchValue(String key) {
        getConfig();
        return config.getProperty(key);
    }

    /**
     * 通过key获取value
     * @param key
     * @return value
     * @see #getConfigValue(String)
     */
    public static String getConfigValueByKey(String key) {
        getConfig();
        return config.getProperty(key);
    }

}
