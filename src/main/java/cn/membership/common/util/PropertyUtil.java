package cn.membership.common.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyUtil {

    public static Properties getProperties(String filePath) {
        Properties props = null;
        InputStream in = null;
        try {
            props = new Properties();
            in = PropertyUtil.class.getResourceAsStream(filePath);
            props.load(new InputStreamReader(in, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    public static String getPropertyValue(String filePath, String key) {
        String value = null;
        Properties properties = getProperties(filePath);
        if(null != properties){
            value = properties.getProperty(key);
        }
        return value;
    }

}
