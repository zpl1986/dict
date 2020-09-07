/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao;

import java.util.List;
import java.util.Map;



/**
 * joinDAO接口
 *
 * @author  zhongpeiliang
 * @version 0.0.1
 * @since
 */
public interface IJoinQueryDao{
    public List<Map<String, Object>> select(final boolean distinct, final String table, final List<String> columnList, 
            final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, 
            final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, 
            final boolean isRandom, final int offset, final int pageSize, final String limit,
            final Map<String, String> joinActionMap, final Map<String,List<String>> joinColumnsMap, final Map<String,Map<String,String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap);
    
    public int count(final boolean distinct, final String table, final List<String> columnList, 
            final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, 
            final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, 
            final boolean isRandom, final int offset, final int pageSize, final String limit,
            final Map<String, String> joinActionMap, final Map<String,List<String>> joinColumnsMap, final Map<String,Map<String,String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap);

    public List<Map<String, Object>> queryList(final String sql, final Map<String, Object> paramMap);

}
