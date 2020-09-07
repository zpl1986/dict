/**
 *
 * Copyright (c) 2016 Chutong Technologies All rights reserved.
 *
 */

package cn.membership.common.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 提供便捷的静态方法获取message内容
 * @author
 *
 */
public class MsgUtil {

    private static String env = ConfigUtil.getEnv();

    private static Properties msg = new Properties();

    /**
     * 重新装载Message.properties
     */
    private static void reloadMsgPropertiesFile() {
        InputStream in = MsgUtil.class.getResourceAsStream("/Message.properties");
        try {
            msg.load(new InputStreamReader(in, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提供静态方法，通过key获取message内容
     * @param key
     * @return
     */
    public static String getMsgContent(String key) {
        if (0 == msg.size() || !"prod".equals(env)) {
            reloadMsgPropertiesFile();
        }
        
        String msgContent = msg.getProperty(key);
        if (msgContent == null || "".equals(msgContent)){
        	msgContent = key;
        }
        return msgContent;
    }

}
