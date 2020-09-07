package cn.membership.web.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import cn.membership.common.util.NamingUtil;
import cn.membership.web.dao.IEntityDao;

public abstract class EntityDao<T> extends BaseDao implements IEntityDao<T> {

	protected Map<String, Field> getEntityVOFieldMap() {
		Map<String, Field> entityVOFieldMap = new HashMap<String, Field>();

		try {
			Class entityVOClass = Class.forName(getEntityVOClassName());
			if (null != entityVOClass) {
				Field[] entityVOClassFields = entityVOClass.getDeclaredFields();
				if (null != entityVOClassFields && 0 != entityVOClassFields.length) {
					for (Field field : entityVOClassFields) {
						if (null != field) {
							String fieldName = field.getName();
							Class fieldType = field.getType();
							if (null != fieldName && null != fieldType && Class.class != fieldType) {
								entityVOFieldMap.put(fieldName, field);
							} // if (null != fieldName && null != fieldType &&
								// Class.class != fieldType)

						} // if (null != field)
					} // for (Field field : entityVOClassFields)
				} // if (null != entityVOClassFields)

			} // if (null != entityVOClass)
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return entityVOFieldMap;
	}

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

		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		if (null != type) {
			Type[] actualTypeArguments = type.getActualTypeArguments();
			if (null != actualTypeArguments && actualTypeArguments.length > 0) {
				entityVOClassName = actualTypeArguments[0].toString();
				entityVOClassName = entityVOClassName.substring(entityVOClassName.lastIndexOf(" ") + 1);
			} // if (null != actualTypeArguments && actualTypeArguments.length >
				// 0)

		} // if (null != type)

		return entityVOClassName;
	}

	private String getTableName() {
		return NamingUtil.convertPascalCase2LowerCaseWithUnderscore(getEntityVOClassSimpleName());
	}

	@Override
	public long insert(T entity) {
		Map<String, Object> convertObject2Map = convertObject2Map(entity);
		convertObject2Map.put("created_at", new Date());
		return insert(convertObject2Map);
	}

	@Override
	public long insert(Map<String, Object> valueArgMap) {

		long insertId = 0;

		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {

			Map<String, Object> validValueArgMap = null;
			if (null != valueArgMap && !valueArgMap.isEmpty()) {
				validValueArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> valueArgEntryIter = valueArgMap.entrySet().iterator();
				while (valueArgEntryIter.hasNext()) {
					Map.Entry<String, Object> valueArgEntry = valueArgEntryIter.next();
					if (null != valueArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(valueArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validValueArgMap.put(valueArgEntry.getKey(), valueArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != valueArgEntry)
				} // while (valueArgEntryIter.hasNext())
			} // if (null != valueArgMap && !valueArgMap.isEmpty())

			insertId = insert(getTableName(), validValueArgMap);
		} // if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty())

		return insertId;
	}

	@Override
	public int delete(final Map<String, Object> whereArgMap) {
		return delete(whereArgMap, null, null);
	}

	@Override
	public int delete(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
		return delete(whereArgMap, likeArgMap, rangeArgMap, null);
	}

	@Override
	public int delete(final String whereClause) {
		return delete(null, null, null, whereClause);
	}

	protected int delete(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {
		int affectedRowsCount = 0;

		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {

			Map<String, Object> validWhereArgMap = null;
			if (null != whereArgMap && !whereArgMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = whereArgMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != whereArgEntry)
				} // while (whereArgEntryIter.hasNext())
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			Map<String, Object> validLikeArgMap = null;
			if (null != likeArgMap && !likeArgMap.isEmpty()) {
				validLikeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> likeArgEntryIter = likeArgMap.entrySet().iterator();
				while (likeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> likeArgEntry = likeArgEntryIter.next();
					if (null != likeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(likeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && String.class == fieldType) {
									validLikeArgMap.put(likeArgEntry.getKey(), likeArgEntry.getValue());
								} // if (null != fieldType && String.class ==
									// fieldType)
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != likeArgEntry)
				} // while (likeArgEntryIter.hasNext())
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			Map<String, Object> validRangeArgMap = null;
			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				validRangeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> rangeArgEntryIter = rangeArgMap.entrySet().iterator();
				while (rangeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> rangeArgEntry = rangeArgEntryIter.next();
					if (null != rangeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(rangeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Date.class == fieldType)) {
									validRangeArgMap.put(rangeArgEntry.getKey(), rangeArgEntry.getValue());
								} // if (null != fieldType && (Integer.class ==
									// fieldType || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != rangeArgEntry)
				} // while (rangeArgEntryIter.hasNext())
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			if ((null != validWhereArgMap && validWhereArgMap.size() > 0) || (null != validLikeArgMap && validLikeArgMap.size() > 0) || (null != validRangeArgMap && validRangeArgMap.size() > 0) || StringUtils.hasLength(whereClause)) {
				affectedRowsCount = delete(getTableName(), validWhereArgMap, validLikeArgMap, validRangeArgMap, whereClause);
			}
		} // if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty())

		return affectedRowsCount;
	}

	@Override
	public int update(final T entity, final Map<String, Object> whereArgMap) {
		return update(entity, whereArgMap, null, null);
	}

	@Override
	public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap) {
		return update(valueArgMap, whereArgMap, null, null);
	}

	@Override
	public int update(final T entity, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
		return update(entity, whereArgMap, likeArgMap, rangeArgMap, null);
	}

	@Override
	public int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
		return update(valueArgMap, whereArgMap, likeArgMap, rangeArgMap, null);
	}

	@Override
	public int update(final T entity, final String whereClause) {
		return update(entity, null, null, null, whereClause);
	}

	@Override
	public int update(final Map<String, Object> valueArgMap, final String whereClause) {
		return update(valueArgMap, null, null, null, whereClause);
	}

	protected int update(final T entity, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {
		return update(convertObject2Map(entity), whereArgMap, likeArgMap, rangeArgMap, whereClause);
	}

	protected int update(final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {
		int affectedRowsCount = 0;

		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {

			Map<String, Object> validValueArgMap = null;
			if (null != valueArgMap && !valueArgMap.isEmpty()) {
				validValueArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> valueArgEntryIter = valueArgMap.entrySet().iterator();
				while (valueArgEntryIter.hasNext()) {
					Map.Entry<String, Object> valueArgEntry = valueArgEntryIter.next();
					if (null != valueArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(valueArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validValueArgMap.put(valueArgEntry.getKey(), valueArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != valueArgEntry)
				} // while (valueArgEntryIter.hasNext())
			} // if (null != valueArgMap && !valueArgMap.isEmpty())

			Map<String, Object> validWhereArgMap = null;
			if (null != whereArgMap && !whereArgMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = whereArgMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != whereArgEntry)
				} // while (whereArgEntryIter.hasNext())
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			Map<String, Object> validLikeArgMap = null;
			if (null != likeArgMap && !likeArgMap.isEmpty()) {
				validLikeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> likeArgEntryIter = likeArgMap.entrySet().iterator();
				while (likeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> likeArgEntry = likeArgEntryIter.next();
					if (null != likeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(likeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && String.class == fieldType) {
									validLikeArgMap.put(likeArgEntry.getKey(), likeArgEntry.getValue());
								} // if (null != fieldType && String.class ==
									// fieldType)
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != likeArgEntry)
				} // while (likeArgEntryIter.hasNext())
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			Map<String, Object> validRangeArgMap = null;
			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				validRangeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> rangeArgEntryIter = rangeArgMap.entrySet().iterator();
				while (rangeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> rangeArgEntry = rangeArgEntryIter.next();
					if (null != rangeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(rangeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Date.class == fieldType)) {
									validRangeArgMap.put(rangeArgEntry.getKey(), rangeArgEntry.getValue());
								} // if (null != fieldType && (Integer.class ==
									// fieldType || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != rangeArgEntry)
				} // while (rangeArgEntryIter.hasNext())
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			affectedRowsCount = update(getTableName(), validValueArgMap, validWhereArgMap, validLikeArgMap, validRangeArgMap, whereClause);
		} // if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty())

		return affectedRowsCount;
	}

	@Override
	public List<T> select(final int offset, final int pageSize) {
		return select(offset, pageSize, null);
	}

	@Override
	public List<T> select(final int offset, final int pageSize, final Map<String, Object> whereArgMap) {
		return select(false, offset, pageSize, whereArgMap, null, null, null);
	}

	@Override
	public List<T> select(final boolean isRandom, final int offset, final int pageSize, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final Map<String, Object> sortArgMap) {
		return select(isRandom, offset, pageSize, whereArgMap, likeArgMap, rangeArgMap, null, sortArgMap);
	}

	@Override
	public List<T> select(final boolean isRandom, final int offset, final int pageSize, final String whereClause, final Map<String, Object> sortArgMap) {
		return select(isRandom, offset, pageSize, null, null, null, whereClause, sortArgMap);
	}

	protected List<T> select(final boolean isRandom, final int offset, final int pageSize, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final Map<String, Object> sortArgMap) {
		List<T> entityList = null;

		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {

			Map<String, Object> validWhereArgMap = null;
			if (null != whereArgMap && !whereArgMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = whereArgMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != whereArgEntry)
				} // while (whereArgEntryIter.hasNext())
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			Map<String, Object> validLikeArgMap = null;
			if (null != likeArgMap && !likeArgMap.isEmpty()) {
				validLikeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> likeArgEntryIter = likeArgMap.entrySet().iterator();
				while (likeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> likeArgEntry = likeArgEntryIter.next();
					if (null != likeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(likeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && String.class == fieldType) {
									validLikeArgMap.put(likeArgEntry.getKey(), likeArgEntry.getValue());
								} // if (null != fieldType && String.class ==
									// fieldType)
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != likeArgEntry)
				} // while (likeArgEntryIter.hasNext())
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			Map<String, Object> validRangeArgMap = null;
			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				validRangeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> rangeArgEntryIter = rangeArgMap.entrySet().iterator();
				while (rangeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> rangeArgEntry = rangeArgEntryIter.next();
					if (null != rangeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(rangeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Date.class == fieldType)) {
									validRangeArgMap.put(rangeArgEntry.getKey(), rangeArgEntry.getValue());
								} // if (null != fieldType && (Integer.class ==
									// fieldType || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != rangeArgEntry)
				} // while (rangeArgEntryIter.hasNext())
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			Map<String, Object> validSortArgMap = null;
			if (null != sortArgMap && !sortArgMap.isEmpty()) {
				validSortArgMap = new LinkedHashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> sortArgEntryIter = sortArgMap.entrySet().iterator();
				while (sortArgEntryIter.hasNext()) {
					Map.Entry<String, Object> sortArgEntry = sortArgEntryIter.next();
					if (null != sortArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(sortArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validSortArgMap.put(sortArgEntry.getKey(), sortArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != sortArgEntry)
				} // while (sortArgEntryIter.hasNext())
			} // if (null != sortArgMap && !sortArgMap.isEmpty())

			final String entityVOClassName = getEntityVOClassName();
			if (null != entityVOClassName) {

				List<Map<String, Object>> recordList = select(false, null, getTableName(), null, validWhereArgMap, validLikeArgMap, validRangeArgMap, whereClause, null, null, validSortArgMap, isRandom, offset, pageSize, null);
				if (null != recordList && !recordList.isEmpty()) {
					entityList = new ArrayList<T>();
					for (Map<String, Object> recordMap : recordList) {
						try {
							T entity = (T) Class.forName(entityVOClassName).newInstance();
							convertMap2Bean(recordMap, entity);
							entityList.add(entity);
						} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} // for (Map<String, Object> recordMap : recordList)
				} // if (null != recordList && !recordList.isEmpty())
			} // if (null != entityVOClassName)

		} // if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty())

		return entityList;
	}

	@Override
	public int count() {
		return count(null, null, null);
	}

	@Override
	public int count(final Map<String, Object> whereArgMap) {
		return count(whereArgMap, null, null);
	}

	@Override
	public int count(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
		return count(whereArgMap, likeArgMap, rangeArgMap, null);
	}

	@Override
	public int count(final String whereClause) {
		return count(null, null, null, whereClause);
	}

	protected int count(final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {

		int rowTotalCount = 0;

		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {

			Map<String, Object> validWhereArgMap = null;
			if (null != whereArgMap && !whereArgMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = whereArgMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != whereArgEntry)
				} // while (whereArgEntryIter.hasNext())
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			Map<String, Object> validLikeArgMap = null;
			if (null != likeArgMap && !likeArgMap.isEmpty()) {
				validLikeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> likeArgEntryIter = likeArgMap.entrySet().iterator();
				while (likeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> likeArgEntry = likeArgEntryIter.next();
					if (null != likeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(likeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && String.class == fieldType) {
									validLikeArgMap.put(likeArgEntry.getKey(), likeArgEntry.getValue());
								} // if (null != fieldType && String.class ==
									// fieldType)
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != likeArgEntry)
				} // while (likeArgEntryIter.hasNext())
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			Map<String, Object> validRangeArgMap = null;
			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				validRangeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> rangeArgEntryIter = rangeArgMap.entrySet().iterator();
				while (rangeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> rangeArgEntry = rangeArgEntryIter.next();
					if (null != rangeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(rangeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Date.class == fieldType)) {
									validRangeArgMap.put(rangeArgEntry.getKey(), rangeArgEntry.getValue());
								} // if (null != fieldType && (Integer.class ==
									// fieldType || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != rangeArgEntry)
				} // while (rangeArgEntryIter.hasNext())
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			rowTotalCount = count(null, getTableName(), validWhereArgMap, validLikeArgMap, validRangeArgMap, whereClause, null, null);

		} // if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty())

		return rowTotalCount;
	}

	public BigDecimal sum(final String sumField, final Map<String, Object> whereArgMap) {
		return sum(sumField, whereArgMap, null, null, null);
	}

	public BigDecimal sum(final String sumField, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap) {
		return sum(sumField, whereArgMap, likeArgMap, rangeArgMap, null);
	}

	public BigDecimal sum(final String sumField, final String whereClause) {
		return sum(sumField, null, null, null, whereClause);
	}

	protected BigDecimal sum(final String sumField, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {

		BigDecimal fieldTotalSum = BigDecimal.ZERO;

		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {

			Map<String, Object> validWhereArgMap = null;
			if (null != whereArgMap && !whereArgMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = whereArgMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								} // if (null != fieldType && (String.class ==
									// fieldType || Integer.class == fieldType
									// || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Boolean.class ==
									// fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != whereArgEntry)
				} // while (whereArgEntryIter.hasNext())
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			Map<String, Object> validLikeArgMap = null;
			if (null != likeArgMap && !likeArgMap.isEmpty()) {
				validLikeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> likeArgEntryIter = likeArgMap.entrySet().iterator();
				while (likeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> likeArgEntry = likeArgEntryIter.next();
					if (null != likeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(likeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && String.class == fieldType) {
									validLikeArgMap.put(likeArgEntry.getKey(), likeArgEntry.getValue());
								} // if (null != fieldType && String.class ==
									// fieldType)
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != likeArgEntry)
				} // while (likeArgEntryIter.hasNext())
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			Map<String, Object> validRangeArgMap = null;
			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				validRangeArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> rangeArgEntryIter = rangeArgMap.entrySet().iterator();
				while (rangeArgEntryIter.hasNext()) {
					Map.Entry<String, Object> rangeArgEntry = rangeArgEntryIter.next();
					if (null != rangeArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(rangeArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Date.class == fieldType)) {
									validRangeArgMap.put(rangeArgEntry.getKey(), rangeArgEntry.getValue());
								} // if (null != fieldType && (Integer.class ==
									// fieldType || Long.class == fieldType ||
									// Double.class == fieldType || Float.class
									// == fieldType || Date.class == fieldType))
							} // if (null != field)
						} // if (entityVOFieldMap.containsKey(fieldName))
					} // if (null != rangeArgEntry)
				} // while (rangeArgEntryIter.hasNext())
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())
			fieldTotalSum = sum(entityVOFieldMap.containsKey(sumField) ? NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sumField) : "", null, getTableName(), validWhereArgMap, validLikeArgMap, validRangeArgMap, whereClause, null, null);

		} // if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty())

		return fieldTotalSum;
	}

	public int count(String sql, Map<String, Object> paramMap) {
		return super.queryForInt(sql, paramMap);
	}

	@SuppressWarnings("all")
	public List<T> fetchEntityListBySql(String sql, Map<String, Object> paramMap) {
		List<T> entityList = null;
		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {
			Map<String, Object> validWhereArgMap = null;
			if (null != paramMap && !paramMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = paramMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								}
							}
						}
					}
				}
			}
			final String entityVOClassName = getEntityVOClassName();
			if (null != entityVOClassName) {
				List<Map<String, Object>> recordList = super.queryForList(sql, validWhereArgMap);
				if (null != recordList && !recordList.isEmpty()) {
					entityList = new ArrayList<T>();
					for (Map<String, Object> recordMap : recordList) {
						try {
							T entity = (T) Class.forName(entityVOClassName).newInstance();
							convertMap2Bean(recordMap, entity);
							entityList.add(entity);
						} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
		return entityList;
	}

	@SuppressWarnings("all")
	public List<T> fetchEntityListBySqlCondition(String sql, Map<String, Object> paramMap) {
		List<T> entityList = null;
		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {
			final String entityVOClassName = getEntityVOClassName();
			if (null != entityVOClassName) {
				List<Map<String, Object>> recordList = super.queryForList(sql, paramMap);
				if (null != recordList && !recordList.isEmpty()) {
					entityList = new ArrayList<T>();
					for (Map<String, Object> recordMap : recordList) {
						try {
							T entity = (T) Class.forName(entityVOClassName).newInstance();
							convertMap2Bean(recordMap, entity);
							entityList.add(entity);
						} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
		return entityList;
	}

	public List<Map<String, Object>> fetchListMapBySql(String sql, Map<String, Object> whereArgMap) {
		List<Map<String, Object>> recordList = null;
		final Map<String, Field> entityVOFieldMap = getEntityVOFieldMap();
		if (null != entityVOFieldMap && !entityVOFieldMap.isEmpty()) {
			Map<String, Object> validWhereArgMap = null;
			if (null != whereArgMap && !whereArgMap.isEmpty()) {
				validWhereArgMap = new HashMap<String, Object>();
				Iterator<Map.Entry<String, Object>> whereArgEntryIter = whereArgMap.entrySet().iterator();
				while (whereArgEntryIter.hasNext()) {
					Map.Entry<String, Object> whereArgEntry = whereArgEntryIter.next();
					if (null != whereArgEntry) {
						final String fieldName = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(whereArgEntry.getKey());
						if (entityVOFieldMap.containsKey(fieldName)) {
							final Field field = entityVOFieldMap.get(fieldName);
							if (null != field) {
								final Class<?> fieldType = field.getType();
								if (null != fieldType && (String.class == fieldType || Integer.class == fieldType || Long.class == fieldType || Double.class == fieldType || BigDecimal.class == fieldType || Float.class == fieldType || Boolean.class == fieldType || Date.class == fieldType)) {
									validWhereArgMap.put(whereArgEntry.getKey(), whereArgEntry.getValue());
								}
							}
						}
					}
				}
			}
			final String entityVOClassName = getEntityVOClassName();
			if (null != entityVOClassName) {
				recordList = super.queryForList(sql, validWhereArgMap);
			}
		}
		return recordList;
	}

	public List<Map<String, Object>> fetchListMapBySqlCondition(String sql, Map<String, Object> paramMap) {
		List<Map<String, Object>> recordList = super.queryForList(sql, paramMap);
		return recordList;
	}

	public int updateBySql(String sql, Map<String, Object> paramMap) {
		return super.update(sql, paramMap);
	}

	public long insertBySql(String sql, Map<String, Object> paramMap) {
		return super.insertSql(sql, paramMap);
	}

	@Override
	public int update(final Long id, final Map<String, Object> valueArgMap) {
		String table = getTableName();
		Map<String, Object> whereArgMap = new HashMap<String, Object>();
		whereArgMap.put("id", id);
		return super.update(table, valueArgMap, whereArgMap, null, null, null);
	}

	@Override
	public long insertExcludeVo(final Map<String, Object> valueArgMap) {
		long insertId = insert(getTableName(), valueArgMap);
		return insertId;
	}

	@Override
	public void batchInsert(List<T> list) {
		if (null != list && list.size() > 0) {
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (T bean : list) {
				Map<String, Object> map = convertObject2Map(bean);
				mapList.add(map);
			}
			super.batchInsert(getTableName(), mapList);
		}
	}
}
