package cn.membership.web.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IEntityService<T, TDAO> {
    
    public String getEntityVOClassSimpleName();

    public String getEntityVOClassName();

    public long create(final T entity);
    
//    public long create(final Map<String, Object> valueArgMap);    
    
    public int remove(final Map<String, Object> whereArgMap);
    
    public int remove(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

//    public int update(final T entity, final Map<String, Object> whereArgMap);
    
    public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap);
    
//    public int update(final T entity, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);

    public List<T> fetch(final int offset, final int pageSize);
    
    public List<T> fetch(final int offset, final int pageSize, final Map<String, Object> whereArgMap);
    
    public List<T> fetch(boolean isRandom, int offset, int pageSize, Map<String, Object> whereArgMap, Map<String, Object> likeArgMap, Map<String, Object> rangeArgMap, Map<String, Object> sortArgMap);
    
    public int count();
    
    public int count(final Map<String, Object> whereArgMap);
    
    public int count(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);
    
    public T fetchDetail(final Long entityId);
    
    public BigDecimal sum(final String sumField, final Map<String, Object> whereArgMap);
    
    public BigDecimal sum(final String sumField,final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap);
    
    public List<T> fetchEntityListBySql(String sql,Map<String, Object> whereArgMap);
}
