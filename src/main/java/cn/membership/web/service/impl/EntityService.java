package cn.membership.web.service.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.membership.common.util.NamingUtil;
import cn.membership.common.util.ReflectionUtil;
import cn.membership.web.dao.IEntityDao;
import cn.membership.web.service.IEntityService;

public abstract class EntityService<T, TDAO> extends BaseService implements IEntityService<T, TDAO> {

    @Override
    public String getEntityVOClassSimpleName() {
        String entityVOClassSimpleName = null;

        String entityVOClassName = getEntityVOClassName();
        if (null != entityVOClassName) {
            entityVOClassSimpleName = entityVOClassName.substring(entityVOClassName.lastIndexOf(".") + 1);
        } // if (null != entityVOClassName)

        return entityVOClassSimpleName;
    }

    @Override
    public String getEntityVOClassName() {
        String entityVOClassName = null;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            String entityVOClassNameFromDao = entityDao.getEntityVOClassName();
            if (null != entityVOClassNameFromDao) {
                ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
                if (null != type) {
                    Type[] actualTypeArguments = type.getActualTypeArguments();
                    if (null != actualTypeArguments && actualTypeArguments.length > 0) {
                        entityVOClassName = actualTypeArguments[0].toString();
                        entityVOClassName = entityVOClassName.substring(entityVOClassName.lastIndexOf(" ") + 1);
                    } // if (null != actualTypeArguments && actualTypeArguments.length > 0)

                } // if (null != type)    

                if (!entityVOClassNameFromDao.equals(entityVOClassName)) {
                    entityVOClassName = null;
                } // if (!entityVOClassNameFromDao.equals(entityVOClassName))

            } // if (null != entityVOClassNameFromDao)
        } // if (null != entityDao)    

        return entityVOClassName;
    }

    protected String getEntityDAOClassSimpleName() {
        String entityDAOClassSimpleName = null;

        String entityDAOClassName = getEntityDAOClassName();
        if (null != entityDAOClassName) {
            entityDAOClassSimpleName = entityDAOClassName.substring(entityDAOClassName.lastIndexOf(".") + 1);
        } // if (null != entityDAOClassName)

        return entityDAOClassSimpleName;
    }

    protected String getEntityDAOClassName() {
        String entityDAOClassName = null;

        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        if (null != type) {
            Type[] actualTypeArguments = type.getActualTypeArguments();
            if (null != actualTypeArguments && actualTypeArguments.length > 1) {
                entityDAOClassName = actualTypeArguments[1].toString();
                entityDAOClassName = entityDAOClassName.substring(entityDAOClassName.lastIndexOf(" ") + 1);
            } // if (null != actualTypeArguments && actualTypeArguments.length > 0)

        } // if (null != type)        

        return entityDAOClassName;
    }

    protected <T> IEntityDao<T> getEntityDaoWithEntityClass(final Class<T> entityClass) {
        IEntityDao<T> entityDao = null;

        final String entityClassSimpleName = entityClass.getSimpleName();

        if (null != entityClassSimpleName) {
            try {
                Field field = getClass().getDeclaredField(entityClassSimpleName.substring(0, 1).toLowerCase().concat(entityClassSimpleName.substring(1)).concat("Dao"));
                if (null != field) {
                    field.setAccessible(true);
                    entityDao = (IEntityDao<T>) field.get(this);
                } // if (null != field)
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } // if (null != entityDAOClassSimpleName)        

        return entityDao;
    }

    protected <T> IEntityService getEntityServiceWithEntityClass(final Class<T> entityClass) {
        IEntityService entityService = null;

        final String entityClassSimpleName = entityClass.getSimpleName();

        if (null != entityClassSimpleName) {
            try {
                Field field = getClass().getDeclaredField(entityClassSimpleName.substring(0, 1).toLowerCase().concat(entityClassSimpleName.substring(1)).concat("Service"));
                if (null != field) {
                    field.setAccessible(true);
                    entityService = (IEntityService) field.get(this);
                } // if (null != field)
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } // if (null != entityDAOClassSimpleName)        

        return entityService;
    }

    protected IEntityDao<T> getEntityDao() {

        IEntityDao<T> entityDao = null;

        String entityDAOClassSimpleName = getEntityDAOClassSimpleName();
        if (null != entityDAOClassSimpleName) {
            try {
                Field field = getClass().getDeclaredField(entityDAOClassSimpleName.substring(0, 1).toLowerCase().concat(entityDAOClassSimpleName.substring(1)));
                if (null != field) {
                    field.setAccessible(true);
                    entityDao = (IEntityDao<T>) field.get(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if (null != entityDAOClassSimpleName)

        return entityDao;
    }

    @Override
    public long create(final T entity) {

        long createdRecordId = 0;

        if (null != entity) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {

                    Method getter = property.getReadMethod();
                    Object subEntity = getter.invoke(entity);
                    if (null != subEntity) {
                        Class<?> propertyClass = property.getPropertyType();

                        if (null != propertyClass && Class.class != propertyClass && String.class != propertyClass && Integer.class != propertyClass && Long.class != propertyClass && Double.class != propertyClass && Float.class != propertyClass && Boolean.class != propertyClass && Date.class != propertyClass && List.class != propertyClass && Map.class != propertyClass && Set.class != propertyClass) {
                            final String subEntityPropertyName = property.getName();
                            if (null != subEntityPropertyName) {

                                final Field subEntityIdField = ReflectionUtil.getDeclaredField(entity, subEntityPropertyName.concat("Id"));
                                final Object subEntityId = ReflectionUtil.getFieldValue(entity, subEntityPropertyName.concat("Id"));

                                if (null != subEntityIdField && (null == subEntityId)) {
                                    IEntityService subEntityService = getEntityServiceWithEntityClass(propertyClass);
                                    if (null != subEntityService) {
                                        Long subEntityCreatedId = subEntityService.create(subEntity);
                                        if (null != subEntityCreatedId && 0L != subEntityCreatedId) {
                                            ReflectionUtil.setFieldValue(entity, subEntityPropertyName.concat("Id"), subEntityCreatedId);
                                            ReflectionUtil.setFieldValue(entity, subEntityPropertyName.concat("Name"), ReflectionUtil.getFieldValue(subEntity, "name"));
                                        } // if (null != subEntityCreatedId && 0L != subEntityCreatedId)

                                    } // if (null != subEntityService)  
                                } // if (null != subEntityIdField && (null == subEntityId))   
                            } // if (null != subEntityPropertyName)
                        } // if (null != propertyClass && Class.class != propertyClass && String.class != propertyClass && Integer.class != propertyClass && Long.class != propertyClass && Double.class != propertyClass && Float.class != propertyClass && Boolean.class != propertyClass && Date.class != propertyClass && List.class != propertyClass && Map.class != propertyClass && Set.class != propertyClass)                        
                    } // if (null != subEntity)

                } // for (PropertyDescriptor property : propertyDescriptors)
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } // if (null != entity)

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            createdRecordId = entityDao.insert(entity);
        } // if (null != entityDao)

        return createdRecordId;
    }

    @Deprecated
    protected long create(final Map<String, Object> valueArgMap) {
        long createdRecordId = 0;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            createdRecordId = entityDao.insert(valueArgMap);
        } // if (null != entityDao)

        return createdRecordId;
    }

    @Override
    public int remove(final Map<String, Object> whereArgMap) {
        return remove(whereArgMap, null, null);
    }

    @Override
    public int remove(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
        int affectedRowsCount = 0;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            affectedRowsCount = entityDao.delete(whereArgMap, likeArgMap, rangeArgMap);
        } // if (null != entityDao)

        return affectedRowsCount;
    }

    @Deprecated
    protected int update(final T entity, final Map<String, Object> whereArgMap) {
        return update(entity, whereArgMap, null, null);
    }

    @Override
    public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap) {
        return update(valueArgMap, whereArgMap, null, null);
    }

    @Deprecated
    protected int update(final T entity, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
        int affectedRowsCount = 0;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            affectedRowsCount = entityDao.update(entity, whereArgMap, likeArgMap, rangeArgMap);
        } // if (null != entityDao)

        return affectedRowsCount;
    }

    @Override
    public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
        int affectedRowsCount = 0;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            affectedRowsCount = entityDao.update(valueArgMap, whereArgMap, likeArgMap, rangeArgMap);
        } // if (null != entityDao)

        return affectedRowsCount;
    }

    @Override
    public List<T> fetch(final int offset, final int pageSize) {
        return fetch(offset, pageSize, null);
    }

    @Override
    public List<T> fetch(final int offset, final int pageSize, final Map<String, Object> whereArgMap) {
        return fetch(false, offset, pageSize, whereArgMap, null, null, null);
    }

    @Override
    public List<T> fetch(final boolean isRandom, final int offset, final int pageSize, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final Map<String, Object> sortArgMap) {
        List<T> entityList = null;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            entityList = entityDao.select(isRandom, offset, pageSize, whereArgMap, likeArgMap, rangeArgMap, sortArgMap);
        } // if (null != entityDao)

        return entityList;
    }

    @Override
    public int count() {
        return count(null);
    }

    @Override
    public int count(final Map<String, Object> whereArgMap) {
        return count(whereArgMap, null, null);
    }

    @Override
    public int count(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
        int rowCount = 0;

        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            rowCount = entityDao.count(whereArgMap, likeArgMap, rangeArgMap);
        } // if (null != entityDao)

        return rowCount;
    }

    @Override
    public T fetchDetail(final Long entityId) {
        T entity = null;

        Map<String, Object> whereArgMap = new HashMap<String, Object>();
        whereArgMap.put("id", entityId);
        List<T> entityList = fetch(0, 1, whereArgMap);
        if (null != entityList && !entityList.isEmpty()) {
            entity = entityList.get(0);
        } // if (null != entityList && !entityList.isEmpty())

        if (null != entity) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {

                    Class<?> propertyClass = property.getPropertyType();
                    if (null != propertyClass && Class.class != propertyClass && String.class != propertyClass && Integer.class != propertyClass && Long.class != propertyClass && Double.class != propertyClass && Float.class != propertyClass && Boolean.class != propertyClass && Date.class != propertyClass && List.class != propertyClass && Map.class != propertyClass && Set.class != propertyClass) {
                        final String subEntityPropertyName = property.getName();
                        if (null != subEntityPropertyName) {

                            final Object subEntityId = ReflectionUtil.getFieldValue(entity, subEntityPropertyName.concat("Id"));
                            if (null != subEntityId && subEntityId instanceof Long) {
                                final IEntityService subEntityService = getEntityServiceWithEntityClass(propertyClass);
                                if (null != subEntityService) {
                                    final Object subEntity = subEntityService.fetchDetail((Long) subEntityId);
                                    if (null != subEntity) {
                                        Method setter = property.getWriteMethod();
                                        setter.invoke(entity, subEntity);

                                        ReflectionUtil.setFieldValue(entity, subEntityPropertyName.concat("Name"), ReflectionUtil.getFieldValue(subEntity, "name"));
                                    } // if (null != subEntity)

                                } // if (null != subEntityService)                                
                            } // if (null != subEntityId && subEntityId instanceof Long)                  
                        } // if (null != subEntityPropertyName)
                    } // if (null != propertyClass && Class.class != propertyClass && String.class != propertyClass && Integer.class != propertyClass && Long.class != propertyClass && Double.class != propertyClass && Float.class != propertyClass && Boolean.class != propertyClass && Date.class != propertyClass && List.class != propertyClass && Map.class != propertyClass && Set.class != propertyClass)
                } // for (PropertyDescriptor property : propertyDescriptors)
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if (null != entity)

        return entity;
    }

    public BigDecimal sum(final String sumField, final Map<String, Object> whereArgMap) {
        return sum(sumField, whereArgMap, null, null);
    }

    public BigDecimal sum(final String sumField, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
        BigDecimal sum = BigDecimal.ZERO;
        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
            sum = entityDao.sum(sumField,whereArgMap, likeArgMap, rangeArgMap);
        } // if (null != entityDao)
        return sum;
    }
    
    
    
    
    public List<T> fetchEntityListBySql(String sql,Map<String, Object> whereArgMap){
        List<T> list = null;
        IEntityDao<T> entityDao = getEntityDao();
        if (null != entityDao) {
               list = entityDao.fetchEntityListBySql(sql, whereArgMap);
        } 
        return list;
    }
    
    
    protected static void convertMap2Bean(Map<String, Object> map, Object bean) {
        if (null != map && null != bean) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                if (null != beanInfo) {
                    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                    if (null != propertyDescriptors) {
                        for (PropertyDescriptor property : propertyDescriptors) {
                            final Class<?> propertyClass = property.getPropertyType();
                            final String propertyName = property.getName();
                            final String key = NamingUtil.convertCamelCase2LowerCaseWithUnderscore(propertyName);

                            if (map.containsKey(key)) {
                                Object value = map.get(key);
                                if (null != value) {
                                    // 得到property对应的setter方法  
                                    Method setter = property.getWriteMethod();
                                    if (Date.class == propertyClass && value instanceof Date) {
                                        setter.invoke(bean, value);
                                    } else if (String.class == propertyClass) {
                                        setter.invoke(bean, (String) value);
                                    } else if (Integer.class == propertyClass) {
                                        setter.invoke(bean, (Integer) value);
                                    } else if (Long.class == propertyClass) {
                                        setter.invoke(bean, (Long) value);
                                    }
                                    //add start
                                    else if (BigDecimal.class == propertyClass) {
                                        setter.invoke(bean, (BigDecimal) value);
                                    }
                                    //add end
                                    else if (Double.class == propertyClass && value instanceof BigDecimal) {
                                        setter.invoke(bean, ((BigDecimal) value).doubleValue());
                                    } else if (Float.class == propertyClass && value instanceof BigDecimal) {
                                        setter.invoke(bean, ((BigDecimal) value).floatValue());
                                    } else if (Boolean.class == propertyClass && value instanceof Integer) {
                                        if (1 == (Integer) value) {
                                            setter.invoke(bean, true);
                                        } else {
                                            setter.invoke(bean, false);
                                        }
                                    }
                                }
                            }   
                        }                          
                    } 
                } 
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } 
    }
    
}
