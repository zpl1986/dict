/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.membership.common.util.FanyiV3Util;
import cn.membership.web.dao.IWordDao;
import cn.membership.web.vo.Word;

/**
 * DAO
 *
 * @author
 * @version 0.0.1
 * @since
 */
@Repository
@Qualifier("wordDao")
public class WordDao extends EntityDao<Word> implements IWordDao {

	@Override
	public Word findById(String key) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String sql = "select * from word where id = :id;";
			paramMap.put("id", key);
			System.out.println("=key==>" + key);
			Map<String, Object> map = namedParameterJdbcTemplate.queryForMap(sql, paramMap);
			Word word = new Word();
			convertMap2Bean(map, word);
			return word;
		} catch (EmptyResultDataAccessException e) {// 没有单词
			String query = FanyiV3Util.query(key);
//			String query = "{\"returnPhrase\":[\"is\"],\"query\":\"is\",\"errorCode\":\"0\",\"l\":\"en2zh-CHS\",\"tSpeakUrl\":\"http://openapi.youdao.com/ttsapi?q=%E6%98%AF&langType=zh-CHS&sign=490837584AC771B30D20BE53E3E6338B&salt=1599403745997&voice=4&format=mp3&appKey=53051efc19d69936\",\"web\":[{\"value\":[\"冰岛\",\"无限斯特拉托斯\",\"影像稳定器\",\"就是\"],\"key\":\"is\"},{\"value\":[\"这是\",\"万圣节\",\"至今都\",\"林晓培\"],\"key\":\"This is\"},{\"value\":[\"很值得\",\"是值得的\",\"艾尔沃思\",\"都值得\"],\"key\":\"Is worth\"}],\"requestId\":\"b5220c75-fda1-41f8-8904-bd7c3343b378\",\"translation\":[\"是\"],\"dict\":{\"url\":\"yddict://m.youdao.com/dict?le=eng&q=is\"},\"webdict\":{\"url\":\"http://m.youdao.com/dict?le=eng&q=is\"},\"basic\":{\"exam_type\":[\"初中\"],\"us-phonetic\":\"ɪz; s; z; əz\",\"phonetic\":\"ɪz; s; z; əz\",\"uk-phonetic\":\"ɪz; s; z; əz\",\"uk-speech\":\"http://openapi.youdao.com/ttsapi?q=is&langType=en&sign=DBC1925A304EE2F0E4A063CE6ACEB049&salt=1599403745997&voice=5&format=mp3&appKey=53051efc19d69936\",\"explains\":[\"v. 是（be的三单形式）\",\"n. 存在\"],\"us-speech\":\"http://openapi.youdao.com/ttsapi?q=is&langType=en&sign=DBC1925A304EE2F0E4A063CE6ACEB049&salt=1599403745997&voice=6&format=mp3&appKey=53051efc19d69936\"},\"isWord\":true,\"speakUrl\":\"http://openapi.youdao.com/ttsapi?q=is&langType=en&sign=DBC1925A304EE2F0E4A063CE6ACEB049&salt=1599403745997&voice=4&format=mp3&appKey=53051efc19d69936\"}";
			Map<String, Object> map = new HashMap<>();
			map.put("id", key);
			map.put("youdao", query);
			JSONObject parse = (JSONObject) JSON.parse(query);
			JSONArray jsonArray = parse.getJSONObject("basic").getJSONArray("explains");
			String content = String.join(";", jsonArray.toArray(new String[0]));
			map.put("content", content);
			insert(map);
			

			Map<String, Object> paramMap = new HashMap<String, Object>();
			String sql = "select * from word where id = :id;";
			paramMap.put("id", key);
			System.out.println("=key==>" + key);
			map = namedParameterJdbcTemplate.queryForMap(sql, paramMap);
			Word word = new Word();
			convertMap2Bean(map, word);
			return word;
		}
	}

}