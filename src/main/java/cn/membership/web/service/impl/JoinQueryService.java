/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.membership.common.util.NamingUtil;
import cn.membership.web.dao.IJoinQueryDao;
import cn.membership.web.service.IJoinQueryService;


/**
 * join服务
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since 	
 */
@Service
@Qualifier("joinQueryService")
public class JoinQueryService implements IJoinQueryService{

    @Autowired
    @Qualifier("joinQueryDao")
    private IJoinQueryDao joinQueryDao;       
    
    public List<Map<String, Object>> fetch(List<String> voList, final boolean distinct, final String table, final List<String> columnList, 
            final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, 
            final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, 
            final boolean isRandom, final int offset, final int pageSize, final String limit,
            final Map<String, String> joinActionMap, final Map<String,List<String>> joinColumnsMap, final Map<String,Map<String,String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap){
        
        Map<String, Object> validWhereArgMap = whereArgMap;
        Map<String, Object> validLikeArgMap = likeArgMap;
        Map<String, Object> validRangeArgMap = rangeArgMap;
        Map<String, Object> validSortArgMap = sortArgMap;
        
        if ((null != whereArgMap || null != likeArgMap || null != rangeArgMap || null != sortArgMap) && (null != voList && voList.size() > 0)) {
            
            Set<String> fields = new HashSet<String>();
           
            for (String vo : voList) {
                try {
                    Class voClass = Class.forName(vo);
                    if (null != voClass) {
                        Field[] voClassFields = voClass.getDeclaredFields();
                        if (null != voClassFields && 0 != voClassFields.length) {
                            for (Field field : voClassFields) {
                                if (null != field) {
                                    String fieldName = field.getName();
                                    Class fieldType = field.getType();
                                    if (null != fieldName && null != fieldType && Class.class != fieldType) {
                                        fields.add(fieldName);
                                    } 
                                }
                            }
                        } 
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            
            if (null != whereArgMap) {
                validWhereArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> whereArgMapEntry : whereArgMap.entrySet()) {
                    if (fields.contains(whereArgMapEntry.getKey())){
                        validWhereArgMap.put(whereArgMapEntry.getKey(), whereArgMapEntry.getValue());
                    }
                }
            }
            
            if (null != likeArgMap) {
                validLikeArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> likeArgMapEntry : likeArgMap.entrySet()) {
                    if (fields.contains(likeArgMapEntry.getKey())){
                        validLikeArgMap.put(likeArgMapEntry.getKey(), likeArgMapEntry.getValue());
                    }
                }
            }
            
            if (null != rangeArgMap) {
                validRangeArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> rangeArgMapEntry : rangeArgMap.entrySet()) {
                    if (fields.contains(rangeArgMapEntry.getKey())){
                        validRangeArgMap.put(rangeArgMapEntry.getKey(), rangeArgMapEntry.getValue());
                    }
                }
            }
            
            if (null != sortArgMap) {
                validSortArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> sortArgMapEntry : sortArgMap.entrySet()) {
                    if (fields.contains(sortArgMapEntry.getKey())){
                        validSortArgMap.put(sortArgMapEntry.getKey(), sortArgMapEntry.getValue());
                    }else if (sortArgMapEntry.getKey().contains(".")){
                        String key[] = sortArgMapEntry.getKey().split("\\.");
                        if (fields.contains(key[1])){
                            validSortArgMap.put(sortArgMapEntry.getKey(), sortArgMapEntry.getValue());
                        }
                    }
                }
            }
            
        }
        
        return convertMap(joinQueryDao.select(distinct, table, columnList, validWhereArgMap, validLikeArgMap, validRangeArgMap, whereClause, groupArgList, havingClause, validSortArgMap, isRandom, offset, pageSize, limit, joinActionMap, joinColumnsMap, joinOnArgsMap, joinWhereArgsMap));
        
    }
    
    public int count(List<String> voList,final boolean distinct, final String table, final List<String> columnList, 
            final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, 
            final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, 
            final boolean isRandom, final int offset, final int pageSize, final String limit,
            final Map<String, String> joinActionMap, final Map<String,List<String>> joinColumnsMap, final Map<String,Map<String,String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap){
        
        Map<String, Object> validWhereArgMap = whereArgMap;
        Map<String, Object> validLikeArgMap = likeArgMap;
        Map<String, Object> validRangeArgMap = rangeArgMap;
        Map<String, Object> validSortArgMap = sortArgMap;
        
        if ((null != whereArgMap || null != likeArgMap || null != rangeArgMap || null != sortArgMap) && (null != voList && voList.size() > 0)) {
            
            Set<String> fields = new HashSet<String>();
           
            for (String vo : voList) {
                try {
                    Class voClass = Class.forName(vo);
                    if (null != voClass) {
                        Field[] voClassFields = voClass.getDeclaredFields();
                        if (null != voClassFields && 0 != voClassFields.length) {
                            for (Field field : voClassFields) {
                                if (null != field) {
                                    String fieldName = field.getName();
                                    Class fieldType = field.getType();
                                    if (null != fieldName && null != fieldType && Class.class != fieldType) {
                                        fields.add(fieldName);
                                    } 
                                }
                            }
                        } 
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            
            if (null != whereArgMap) {
                validWhereArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> whereArgMapEntry : whereArgMap.entrySet()) {
                    if (fields.contains(whereArgMapEntry.getKey())){
                        validWhereArgMap.put(whereArgMapEntry.getKey(), whereArgMapEntry.getValue());
                    }
                }
            }
            
            if (null != likeArgMap) {
                validLikeArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> whereArgMapEntry : likeArgMap.entrySet()) {
                    if (fields.contains(whereArgMapEntry.getKey())){
                        validLikeArgMap.put(whereArgMapEntry.getKey(), whereArgMapEntry.getValue());
                    }
                }
            }
            
            if (null != rangeArgMap) {
                validRangeArgMap = new HashMap<String, Object>();
                for (Entry<String, Object> whereArgMapEntry : rangeArgMap.entrySet()) {
                    if (fields.contains(whereArgMapEntry.getKey())){
                        validRangeArgMap.put(whereArgMapEntry.getKey(), whereArgMapEntry.getValue());
                    }
                }
            }
            
        }
        
        return joinQueryDao.count(distinct, table, columnList, validWhereArgMap, likeArgMap, rangeArgMap, whereClause, groupArgList, havingClause, null, isRandom, offset, pageSize, limit, joinActionMap, joinColumnsMap, joinOnArgsMap, joinWhereArgsMap);
        
    }
    
    protected static List<Map<String, Object>> convertMap(List<Map<String, Object>> list) {
        List<Map<String, Object>> convertedList = null;
        if (null !=list) {
            convertedList = new ArrayList<Map<String, Object>>();            
            for (Map<String, Object> map : list){
                Map<String, Object> convertedMap = new HashMap<String, Object>();
                for (Entry<String, Object> mapEntry : map.entrySet()) {
                    Object value = mapEntry.getValue();
                    Object newValue = null;
                    if (value instanceof Date) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        newValue = dateFormat.format(value);
                    } else if (value instanceof String) {
                        newValue = value;
                    } else if (value instanceof Integer) {
                        newValue = value;
                    } else if (value instanceof Long) {
                        newValue = value;
                    } else if (value instanceof BigDecimal) {
                        newValue = value;
                    } else if (value == null) {
                        newValue = "";
                    } else {
                        newValue = value;
                    }
                    convertedMap.put(NamingUtil.convertLowerCaseWithUnderscore2CamelCase(mapEntry.getKey()), newValue);
                }
                convertedList.add(convertedMap);
            }

        }

        return convertedList;
    }
    
    public List<Map<String, Object>> queryList(final String sql, final Map<String, Object> paramMap){
        return convertMap(joinQueryDao.queryList(sql, paramMap));
    }

}
