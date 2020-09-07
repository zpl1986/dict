package cn.membership.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtil {
    /**
     * 清除值为null的映射
     * 
     * @param map
     * @return
     */
    public static Map<String, Object> deleteNullValue(Map<String, Object> map) {
	if (map == null) {
	    return null;
	}
	Iterator<String> iterator = map.keySet().iterator();
	while (iterator.hasNext()) {
	    String key = iterator.next();
	    if (map.get(key) == null) {
		iterator.remove();
	    }
	}
	return map;
    }

    public static Map<String, Object> transNullToNotNull(Map<String, Object> map) {
	if (map == null) {
	    return null;
	}
	for (String key : map.keySet()) {
	    Object object = map.get(key);
	    if (object == null) {
		map.put(key, "");
	    }
	}
	return map;
    }

    public static void main(String[] args) throws InterruptedException {
	final Map<String, Object> map = new HashMap<String, Object>();
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    map.put(System.currentTimeMillis() % 10 + "", null);
		}
	    }
	}).start();
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    transNullToNotNull(map);
		}
	    }
	}).start();
	// Thread.sleep(1000);

    }
}
