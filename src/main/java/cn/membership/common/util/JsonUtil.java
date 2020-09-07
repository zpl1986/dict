package cn.membership.common.util;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {
	private static ObjectMapper mapper =  null;
	
	/**
	 * 初始化
	 */
	static{
		try{
			if(mapper == null){
				mapper = new ObjectMapper();
			}
		}catch(Exception e){
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static String objectToJson(Object o){
		String result = null;
		try {
			result = mapper.writeValueAsString(o);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> jsonToMap(String json){
		Map<String,Object> map = null;
		try {
			map = mapper.readValue(json, Map.class);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public static String mapToJson(Map<String,Object> map){
		String json = null;
		try {
			json = mapper.writeValueAsString(map);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
}
