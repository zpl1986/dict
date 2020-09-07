/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IEntityDao<T> {

    public String getEntityVOClassSimpleName();

    public String getEntityVOClassName();

    public long insert(final T entity);

    public long insert(final Map<String, Object> valueArgMap);

    public int delete(final Map<String, Object> whereArgMap);

    public int delete(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public int delete(final String whereClause);

    public int update(final T entity, final Map<String, Object> whereArgMap);

    public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap);

    public int update(final T entity, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public int update(final T entity, final String whereClause);

    public int update(final Map<String, Object> valueArgMap, final String whereClause);

    public List<T> select(final int offset, final int pageSize);

    public List<T> select(final int offset, int pageSize, final Map<String, Object> whereArgMap);

    public List<T> select(final boolean isRandom, final int offset, final int pageSize, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final Map<String, Object> sortArgMap);

    public List<T> select(final boolean isRandom, final int offset, final int pageSize, final String whereClause, final Map<String, Object> sortArgMap);

    public int count();
    
    public int count(final Map<String, Object> whereArgMap);

    public int count(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public int count(final String whereClause);
    
    public BigDecimal sum(final String sumField,final Map<String, Object> whereArgMap);

    public BigDecimal sum(final String sumField,final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public BigDecimal sum(final String sumField,final String whereClause);
    
    int count(String sql,Map<String, Object> whereArgMap);
    List<T> fetchEntityListBySql(String sql,Map<String, Object> whereArgMap);
    List<T> fetchEntityListBySqlCondition(String sql,Map<String, Object> whereArgMap);
    List<Map<String, Object>> fetchListMapBySql(String sql,Map<String, Object> whereArgMap);
    List<Map<String, Object>> fetchListMapBySqlCondition(String sql,Map<String, Object> paramMap);
    int updateBySql(String sql,Map<String, Object> paramMap);
    long insertBySql(String sql,Map<String, Object> whereArgMap);
    int update(Long id, Map<String, Object> valueArgMap);
    
    public long insertExcludeVo(final Map<String, Object> valueArgMap);
    
    public void batchInsert(final List<T> list);
}
