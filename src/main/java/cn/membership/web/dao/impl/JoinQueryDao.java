package cn.membership.web.dao.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import cn.membership.web.dao.IJoinQueryDao;

/**
 * joinDAO接口
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since       
 */
@Repository
@Qualifier("joinQueryDao")
public class JoinQueryDao extends BaseDao implements IJoinQueryDao {

   public List<Map<String, Object>> select(final boolean distinct, final String table, final List<String> columnList, 
           final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, 
           final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, 
           final boolean isRandom, final int offset, final int pageSize, final String limit,
           final Map<String, String> joinActionMap, final Map<String,List<String>> joinColumnsMap, final Map<String,Map<String,String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap){
        
        return joinSelect(distinct, table, columnList, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupArgList, havingClause, sortArgMap, 
                isRandom, offset, pageSize, limit, joinActionMap, joinColumnsMap, joinOnArgsMap, joinWhereArgsMap);
   }
   
   public int count(final boolean distinct, final String table, final List<String> columnList, 
           final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, 
           final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, 
           final boolean isRandom, final int offset, final int pageSize, final String limit,
           final Map<String, String> joinActionMap, final Map<String,List<String>> joinColumnsMap, final Map<String,Map<String,String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap){
       
           
        return joinCount(distinct, table, columnList, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupArgList, havingClause, sortArgMap, 
                   isRandom, offset, pageSize, limit, joinActionMap, joinColumnsMap, joinOnArgsMap, joinWhereArgsMap);
   }
   
   public List<Map<String, Object>> queryList(final String sql, final Map<String, Object> paramMap){
       return queryForList(sql,paramMap);
   }


}
