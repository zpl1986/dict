package cn.membership.web.dao.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import cn.membership.common.util.GetAppUtil;
import cn.membership.common.util.NamingUtil;
import cn.membership.common.util.SQLUtil;

public abstract class BaseDao {

	@Autowired
	@Qualifier("namedParameterJdbcTemplate")
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	protected final Logger log = Logger.getLogger(getClass());

	protected JdbcTemplate jdbcTemplate;

	protected SqlParameterSource sqlParameterSource = null;

	private final static Logger LOGGER = Logger.getLogger(BaseDao.class);

	private static final String FLOOR_RANGE_SUFFIX = "Floor";
	private static final String CEILING_RANGE_SUFFIX = "Ceiling";

	private static final Pattern WILDCARD_BEFORE_CHECK_PATTERN = Pattern.compile("^\\*.*");

	private static final Pattern WILDCARD_BEFORE_PATTERN = Pattern.compile("^(\\*)*");

	private static final Pattern WILDCARD_AFTER_CHECK_PATTERN = Pattern.compile(".*\\*$");

	private static final Pattern WILDCARD_AFTER_PATTERN = Pattern.compile("(\\*)*$");

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
										if (null != value) {
											setter.invoke(bean, ((Number) value).intValue());
										}
									} else if (Long.class == propertyClass) {
										setter.invoke(bean, ((Number) value).longValue());
									}
									// add start
									else if (BigDecimal.class == propertyClass) {
										setter.invoke(bean, (BigDecimal) value);
									}
									// add end
									else if (Double.class == propertyClass && value instanceof Double) {
										setter.invoke(bean, (Double) value);
									} else if (Double.class == propertyClass && value instanceof BigDecimal) {
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
								} // if (null != value)
							} // if (map.containsKey(key))
						} // for (PropertyDescriptor property :
							// propertyDescriptors)
					} // if (null != propertyDescriptors)
				} // if (null != beanInfo)
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} // if (null != map && null != bean)
	}

	public static Map<String, Object> convertObject2Map(Object bean) {
		Map<String, Object> map = null;

		if (null != bean) {
			map = new HashMap<String, Object>();

			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				for (PropertyDescriptor property : propertyDescriptors) {
					final Class<?> propertyClass = property.getPropertyType();
					final String propertyName = property.getName();

					// 过滤class属性
					if (null != propertyName && Class.class != propertyClass) {
						final String key = NamingUtil.convertCamelCase2LowerCaseWithUnderscore(propertyName);
						// 得到property对应的getter方法
						Method getter = property.getReadMethod();
						Object value = getter.invoke(bean);
						if (null != value) {
							if (value instanceof String || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof BigDecimal || value instanceof Date) {
								map.put(key, value);
							} else if (value instanceof Boolean) {
								if ((Boolean) value) {
									map.put(key, 1);
								} else {
									map.put(key, 0);
								} // else
							} // else if (value instanceof Boolean)

						} // if (null != value)

					} // if (null != propertyName && Class.class !=
						// propertyClass)

				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} // if (null != bean)

		return map;
	}

	protected final long insert(final String table, final Map<String, Object> valueArgMap) {

		long insertId = 0;

		if (null != table && 0 != table.trim().length() && null != valueArgMap && 0 != valueArgMap.size()) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			final int valueArgMapSize = valueArgMap.size();
			final String[] columns = new String[valueArgMapSize];
			// final Object[] values = new Object[valueArgMapSize];
			final List<String> columnList = new ArrayList<String>(valueArgMapSize);
			// final List<Object> valueList = new
			// ArrayList<Object>(valueArgMapSize);
			for (Map.Entry<String, Object> valueArgEntry : valueArgMap.entrySet()) {
				columnList.add(valueArgEntry.getKey());
				// valueList.add(valueArgEntry.getValue());
			}
			columnList.toArray(columns);
			// valueList.toArray(values);
			paramMap.putAll(valueArgMap);

			final String sql = SQLUtil.buildInsertSqlStr(table, columns);

			if (null != namedParameterJdbcTemplate && !StringUtils.isEmpty(sql) && null != paramMap) {
				try {
					log.debug("sql = " + sql);
					log.debug("paramMap = " + paramMap);
					final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
					namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), generatedKeyHolder);

					final Number generatedKey = generatedKeyHolder.getKey();
					if (null != generatedKey) {
						insertId = generatedKey.longValue();
						log.debug("insertId = " + insertId);
					} // if (null != generatedKey)

				} catch (DataAccessException e) {
					LOGGER.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			} // if (null != namedParameterJdbcTemplate &&
				// !StringUtils.isEmpty(sql) && null != paramMap)

		} // if (null != valueArgMap && 0 != valueArgMap.size())

		return insertId;
	}

	protected final int delete(final String table, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {
		int affectedRowsCount = 0;

		if (null != table && 0 != table.trim().length()) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {

										// List<Object> whereInColumnValueList =
										// null;
										// for (Object whereArgValueListItem :
										// (List)whereArgValue) {
										// if (null != whereArgValueListItem) {
										// if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// {
										// if (null == whereInColumnValueList) {
										// whereInColumnValueList = new
										// ArrayList<Object>();
										// } // if (null ==
										// whereInColumnValueList)
										// whereInColumnValueList.add(whereArgValueListItem);
										// } // if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// } // if (null !=
										// whereArgValueListItem)
										// } // for (Object
										// whereArgValueListItem :
										// (List)whereArgValue)
										//
										// if (null != whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty()) {
										// whereInColumnList.add(whereArgKey);
										// whereParamMap.put(whereArgKey,
										// whereInColumnValueList);
										// } // if (null !=
										// whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty())

										whereInColumnList.add(whereArgKey);
										whereParamMap.put(whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())

					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			final String sql = SQLUtil.buildDeleteSqlStr(table, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause);

			affectedRowsCount = update(sql, paramMap);
		}

		return affectedRowsCount;
	}

	protected final int update(final String table, final Map<String, Object> valueArgMap, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause) {

		int affectedRowsCount = 0;

		if (null != table && 0 != table.trim().length() && null != valueArgMap && 0 != valueArgMap.size()) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			final int valueArgMapSize = valueArgMap.size();
			final String[] columns = new String[valueArgMapSize];
			// final Object[] values = new Object[valueArgMapSize];
			final List<String> columnList = new ArrayList<String>(valueArgMapSize);
			// final List<Object> valueList = new
			// ArrayList<Object>(valueArgMapSize);
			for (Map.Entry<String, Object> valueArgEntry : valueArgMap.entrySet()) {
				columnList.add(valueArgEntry.getKey());
				// valueList.add(valueArgEntry.getValue());
			}
			columnList.toArray(columns);
			// valueList.toArray(values);
			paramMap.putAll(valueArgMap);

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {

										// List<Object> whereInColumnValueList =
										// null;
										// for (Object whereArgValueListItem :
										// (List)whereArgValue) {
										// if (null != whereArgValueListItem) {
										// if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// {
										// if (null == whereInColumnValueList) {
										// whereInColumnValueList = new
										// ArrayList<Object>();
										// } // if (null ==
										// whereInColumnValueList)
										// whereInColumnValueList.add(whereArgValueListItem);
										// } // if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// } // if (null !=
										// whereArgValueListItem)
										// } // for (Object
										// whereArgValueListItem :
										// (List)whereArgValue)
										//
										// if (null != whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty()) {
										// whereInColumnList.add(whereArgKey);
										// whereParamMap.put(whereArgKey,
										// whereInColumnValueList);
										// } // if (null !=
										// whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty())

										whereInColumnList.add(whereArgKey);
										whereParamMap.put(whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())

					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			final String sql = SQLUtil.buildUpdateSqlStr(table, columns, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause);
			log.info("update sql=" + sql);
			affectedRowsCount = update(sql, paramMap);

		} // if (null != valueArgMap && 0 != valueArgMap.size())

		return affectedRowsCount;
	}

	protected final List<Map<String, Object>> select(final boolean distinct, final List<String> tableList, final String table, final List<String> columnList, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, final boolean isRandom, final int offset, final int pageSize, final String limit) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			StringBuffer groupByStrBuf = new StringBuffer();

			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} else {
						groupByStrBuf.append(", ").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)

			if (0 != groupByStrBuf.length()) {
				groupBy = groupByStrBuf.toString();
			} // if (0 != groupByStrBuf.length())
		} // if (null != groupArgList && !groupArgList.isEmpty())

		String orderBy = null;
		if (isRandom) {
			orderBy = "rand()";
		} else if (null != sortArgMap && !sortArgMap.isEmpty()) {

			StringBuffer orderByStrBuf = new StringBuffer();

			for (Map.Entry<String, Object> sortArgEntry : sortArgMap.entrySet()) {

				String sortKey = sortArgEntry.getKey();
				Object sortValueObject = sortArgEntry.getValue();

				if (null != sortKey && null != sortValueObject && sortValueObject instanceof String) {
					if ("ASC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" ASC");

					} else if ("DESC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" DESC");
					}
				} // if (null != sortKey && null != sortValueObject &&
					// sortValueObject instanceof String)
			} // for (Map.Entry<String, Object> sortArgEntry :
				// sortArgMap.entrySet())

			if (0 != orderByStrBuf.length()) {
				orderBy = orderByStrBuf.toString();
			} // if (0 != orderByStrBuf.length())
		} else {
			orderBy = "id DESC";
		}

		return select(distinct, tableList, table, columnList, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit);
	}

	private final List<Map<String, Object>> select(final boolean distinct, final List<String> tableList, final String table, final List<String> columnList, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit) {

		List<Map<String, Object>> rowMapList = null;

		if ((null != table && 0 != table.trim().length()) || (null != tableList && 0 != tableList.size())) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			String[] tables = null;
			if (null != tableList && 0 != tableList.size()) {
				tables = new String[tableList.size()];
				tableList.toArray(tables);
			}

			String[] columns = null;
			if (null != columnList && 0 != columnList.size()) {
				columns = new String[columnList.size()];
				columnList.toArray(columns);
			}

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {

										// List<Object> whereInColumnValueList =
										// null;
										// for (Object whereArgValueListItem :
										// (List)whereArgValue) {
										// if (null != whereArgValueListItem) {
										// if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// {
										// if (null == whereInColumnValueList) {
										// whereInColumnValueList = new
										// ArrayList<Object>();
										// } // if (null ==
										// whereInColumnValueList)
										// whereInColumnValueList.add(whereArgValueListItem);
										// } // if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// } // if (null !=
										// whereArgValueListItem)
										// } // for (Object
										// whereArgValueListItem :
										// (List)whereArgValue)
										//
										// if (null != whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty()) {
										// whereInColumnList.add(whereArgKey);
										// whereParamMap.put(whereArgKey,
										// whereInColumnValueList);
										// } // if (null !=
										// whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty())

										whereInColumnList.add(whereArgKey);
										whereParamMap.put(whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())

					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			final String sql = SQLUtil.buildSelectSqlStr(distinct, tables, table, columns, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit);
			System.out.println(sql);
			rowMapList = queryForList(sql, paramMap);

		} // if ((null != table && 0 != table.trim().length()) || (null !=
			// tableList && 0 != tableList.size()))

		return rowMapList;
	}

	protected final List<Map<String, Object>> select(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, final boolean isRandom, final int offset, final int pageSize, final String limit) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			StringBuffer groupByStrBuf = new StringBuffer();

			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} else {
						groupByStrBuf.append(", ").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)

			if (0 != groupByStrBuf.length()) {
				groupBy = groupByStrBuf.toString();
			} // if (0 != groupByStrBuf.length())
		} // if (null != groupArgList && !groupArgList.isEmpty())

		String orderBy = null;
		if (isRandom) {
			orderBy = "rand()";
		} else if (null != sortArgMap && !sortArgMap.isEmpty()) {

			StringBuffer orderByStrBuf = new StringBuffer();

			for (Map.Entry<String, Object> sortArgEntry : sortArgMap.entrySet()) {

				String sortKey = sortArgEntry.getKey();
				Object sortValueObject = sortArgEntry.getValue();

				if (null != sortKey && null != sortValueObject && sortValueObject instanceof String) {
					if ("ASC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" ASC");

					} else if ("DESC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" DESC");
					}
				} // if (null != sortKey && null != sortValueObject &&
					// sortValueObject instanceof String)
			} // for (Map.Entry<String, Object> sortArgEntry :
				// sortArgMap.entrySet())

			if (0 != orderByStrBuf.length()) {
				orderBy = orderByStrBuf.toString();
			} // if (0 != orderByStrBuf.length())
		} else {
			orderBy = "created_at DESC";
		}

		return select(sql, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit);
	}

	protected final List<Map<String, Object>> select(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, final boolean isRandom, final int offset, final int pageSize, final String limit, final Map<String, Object> outParamMap) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			StringBuffer groupByStrBuf = new StringBuffer();

			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} else {
						groupByStrBuf.append(", ").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)

			if (0 != groupByStrBuf.length()) {
				groupBy = groupByStrBuf.toString();
			} // if (0 != groupByStrBuf.length())
		} // if (null != groupArgList && !groupArgList.isEmpty())

		String orderBy = null;
		if (isRandom) {
			orderBy = "rand()";
		} else if (null != sortArgMap && !sortArgMap.isEmpty()) {

			StringBuffer orderByStrBuf = new StringBuffer();

			for (Map.Entry<String, Object> sortArgEntry : sortArgMap.entrySet()) {

				String sortKey = sortArgEntry.getKey();
				Object sortValueObject = sortArgEntry.getValue();

				if (null != sortKey && null != sortValueObject && sortValueObject instanceof String) {
					if ("ASC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" ASC");

					} else if ("DESC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" DESC");
					}
				} // if (null != sortKey && null != sortValueObject &&
					// sortValueObject instanceof String)
			} // for (Map.Entry<String, Object> sortArgEntry :
				// sortArgMap.entrySet())

			if (0 != orderByStrBuf.length()) {
				orderBy = orderByStrBuf.toString();
			} // if (0 != orderByStrBuf.length())
		} else {
			orderBy = "created_at DESC";
		}

		return select(sql, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit, outParamMap);
	}

	private final List<Map<String, Object>> select(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit) {

		List<Map<String, Object>> rowMapList = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();

		String[] whereColumns = null;
		String[] whereInColumns = null;
		String[] whereLikeColumns = null;
		String[] whereRangeColumns = null;
		if (SQLUtil.isEmpty(whereClause)) {

			if (null != whereArgMap && !whereArgMap.isEmpty()) {

				final List<String> whereColumnList = new ArrayList<String>();
				final List<String> whereInColumnList = new ArrayList<String>();
				final Map<String, Object> whereParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
					if (null != whereArgEntry) {
						String whereArgKey = whereArgEntry.getKey();
						Object whereArgValue = whereArgEntry.getValue();
						if (null != whereArgKey && null != whereArgValue) {
							if (whereArgValue instanceof List) {
								if (!((List) whereArgValue).isEmpty()) {
									whereInColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								} // if (!((List)whereArgValue).isEmpty())
							} else {
								whereColumnList.add(whereArgKey);
								whereParamMap.put(whereArgKey, whereArgValue);
							}
						} // if (null != whereArgKey && null != whereArgValue)
					} // if (null != whereArgEntry)

				} // for (Map.Entry<String, Object> whereArgEntry :
					// whereArgMap.entrySet())

				if (!whereColumnList.isEmpty()) {
					whereColumns = new String[whereColumnList.size()];
					whereColumnList.toArray(whereColumns);
				} // if (!whereColumnList.isEmpty())

				if (!whereInColumnList.isEmpty()) {
					whereInColumns = new String[whereInColumnList.size()];
					whereInColumnList.toArray(whereInColumns);
				} // if (!whereInColumnList.isEmpty())

				paramMap.putAll(whereParamMap);
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			if (null != likeArgMap && !likeArgMap.isEmpty()) {

				final List<String> whereLikeColumnList = new ArrayList<String>();
				final Map<String, Object> likeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

					String likeArgKey = likeArgEntry.getKey();
					Object likeArgValue = likeArgEntry.getValue();
					if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
						String likeArgValueString = (String) likeArgValue;

						boolean hasWildcardBefore = false;
						boolean hasWildcardAfter = false;

						if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardBefore = true;
							likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardAfter = true;
							likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (!hasWildcardBefore && !hasWildcardAfter) {
							likeArgValueString = "%" + likeArgValueString + "%";
						} // if (!hasWildcardBefore && !hasWildcardAfter)

						if (hasWildcardBefore) {
							likeArgValueString = "%" + likeArgValueString;
						} // if (hasWildcardBefore)

						if (hasWildcardAfter) {
							likeArgValueString = likeArgValueString + "%";
						} // if (hasWildcardAfter)

						whereLikeColumnList.add(likeArgKey);
						likeParamMap.put(likeArgKey, likeArgValueString);

					} // if (null != likeArgKey && null != likeValueObject &&
						// likeValueObject instanceof String)
				} // for (Map.Entry<String, Object> likeArgEntry :
					// likeArgMap.entrySet())

				if (!whereLikeColumnList.isEmpty()) {
					whereLikeColumns = new String[whereLikeColumnList.size()];
					whereLikeColumnList.toArray(whereLikeColumns);
				} // if (!whereColumnList.isEmpty())

				paramMap.putAll(likeParamMap);
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				final List<String> whereRangeColumnList = new ArrayList<String>();
				final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
					final String rangeArgKey = rangeArgEntry.getKey();
					final Object rangeArgValue = rangeArgEntry.getValue();
					if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

						Object floorValueObject = ((List) rangeArgValue).get(0);
						Object ceilingValueObject = ((List) rangeArgValue).get(1);
						if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
							if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
								whereRangeColumnList.add(rangeArgEntry.getKey());

								whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
								whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
							} // if (!(floorValueObject instanceof String) ||
								// (!StringUtils.isEmpty(floorValueObject) &&
								// !StringUtils.isEmpty(ceilingValueObject)))
						} // if (null != floorValueObject && null !=
							// ceilingValueObject)

					} // if (null != rangeArgKey && null != rangeArgValue &&
						// rangeArgValue instanceof List && 2 ==
						// ((List)rangeArgValue).size())

				} // for (Map.Entry<String, Object> rangeArgEntry :
					// rangeArgMap.entrySet())

				if (!whereRangeColumnList.isEmpty()) {
					whereRangeColumns = new String[whereRangeColumnList.size()];
					whereRangeColumnList.toArray(whereRangeColumns);
				} // if (!whereRangeColumnList.isEmpty())

				paramMap.putAll(whereRangeParamMap);
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

		} // if (SQLUtil.isEmpty(whereClause))

		final String sqlStr = SQLUtil.buildSelectSqlStr(sql, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit);

		rowMapList = queryForList(sqlStr, paramMap);

		return convertMap(rowMapList);
	}

	private final List<Map<String, Object>> select(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit, final Map<String, Object> outParamMap) {

		List<Map<String, Object>> rowMapList = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();

		if (null != outParamMap) {
			paramMap.putAll(outParamMap);
		}

		String[] whereColumns = null;
		String[] whereInColumns = null;
		String[] whereLikeColumns = null;
		String[] whereRangeColumns = null;
		if (SQLUtil.isEmpty(whereClause)) {

			if (null != whereArgMap && !whereArgMap.isEmpty()) {

				final List<String> whereColumnList = new ArrayList<String>();
				final List<String> whereInColumnList = new ArrayList<String>();
				final Map<String, Object> whereParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
					if (null != whereArgEntry) {
						String whereArgKey = whereArgEntry.getKey();
						Object whereArgValue = whereArgEntry.getValue();
						if (null != whereArgKey && null != whereArgValue) {
							if (whereArgValue instanceof List) {
								if (!((List) whereArgValue).isEmpty()) {
									whereInColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								} // if (!((List)whereArgValue).isEmpty())
							} else {
								whereColumnList.add(whereArgKey);
								whereParamMap.put(whereArgKey, whereArgValue);
							}
						} // if (null != whereArgKey && null != whereArgValue)
					} // if (null != whereArgEntry)

				} // for (Map.Entry<String, Object> whereArgEntry :
					// whereArgMap.entrySet())

				if (!whereColumnList.isEmpty()) {
					whereColumns = new String[whereColumnList.size()];
					whereColumnList.toArray(whereColumns);
				} // if (!whereColumnList.isEmpty())

				if (!whereInColumnList.isEmpty()) {
					whereInColumns = new String[whereInColumnList.size()];
					whereInColumnList.toArray(whereInColumns);
				} // if (!whereInColumnList.isEmpty())

				paramMap.putAll(whereParamMap);
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			if (null != likeArgMap && !likeArgMap.isEmpty()) {

				final List<String> whereLikeColumnList = new ArrayList<String>();
				final Map<String, Object> likeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

					String likeArgKey = likeArgEntry.getKey();
					Object likeArgValue = likeArgEntry.getValue();
					if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
						String likeArgValueString = (String) likeArgValue;

						boolean hasWildcardBefore = false;
						boolean hasWildcardAfter = false;

						if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardBefore = true;
							likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardAfter = true;
							likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (!hasWildcardBefore && !hasWildcardAfter) {
							likeArgValueString = "%" + likeArgValueString + "%";
						} // if (!hasWildcardBefore && !hasWildcardAfter)

						if (hasWildcardBefore) {
							likeArgValueString = "%" + likeArgValueString;
						} // if (hasWildcardBefore)

						if (hasWildcardAfter) {
							likeArgValueString = likeArgValueString + "%";
						} // if (hasWildcardAfter)

						whereLikeColumnList.add(likeArgKey);
						likeParamMap.put(likeArgKey, likeArgValueString);

					} // if (null != likeArgKey && null != likeValueObject &&
						// likeValueObject instanceof String)
				} // for (Map.Entry<String, Object> likeArgEntry :
					// likeArgMap.entrySet())

				if (!whereLikeColumnList.isEmpty()) {
					whereLikeColumns = new String[whereLikeColumnList.size()];
					whereLikeColumnList.toArray(whereLikeColumns);
				} // if (!whereColumnList.isEmpty())

				paramMap.putAll(likeParamMap);
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				final List<String> whereRangeColumnList = new ArrayList<String>();
				final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
					final String rangeArgKey = rangeArgEntry.getKey();
					final Object rangeArgValue = rangeArgEntry.getValue();
					if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

						Object floorValueObject = ((List) rangeArgValue).get(0);
						Object ceilingValueObject = ((List) rangeArgValue).get(1);
						if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
							if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
								whereRangeColumnList.add(rangeArgEntry.getKey());

								whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
								whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
							} // if (!(floorValueObject instanceof String) ||
								// (!StringUtils.isEmpty(floorValueObject) &&
								// !StringUtils.isEmpty(ceilingValueObject)))
						} // if (null != floorValueObject && null !=
							// ceilingValueObject)

					} // if (null != rangeArgKey && null != rangeArgValue &&
						// rangeArgValue instanceof List && 2 ==
						// ((List)rangeArgValue).size())

				} // for (Map.Entry<String, Object> rangeArgEntry :
					// rangeArgMap.entrySet())

				if (!whereRangeColumnList.isEmpty()) {
					whereRangeColumns = new String[whereRangeColumnList.size()];
					whereRangeColumnList.toArray(whereRangeColumns);
				} // if (!whereRangeColumnList.isEmpty())

				paramMap.putAll(whereRangeParamMap);
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

		} // if (SQLUtil.isEmpty(whereClause))

		final String sqlStr = SQLUtil.buildSelectSqlStr(sql, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit);

		rowMapList = queryForList(sqlStr, paramMap);

		return convertMap(rowMapList);
	}

	protected final int count(final List<String> tableList, final String table, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupBy = groupArg;
					} else {
						groupBy.concat(", ").concat(groupArg);
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)
		} // if (null != groupArgList && !groupArgList.isEmpty())

		return count(tableList, table, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause);
	}

	private final int count(final List<String> tableList, final String table, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause) {
		int rowTotalCount = 0;

		if ((null != table && 0 != table.trim().length()) || (null != tableList && 0 != tableList.size())) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			String[] tables = null;
			if (null != tableList && 0 != tableList.size()) {
				tables = new String[tableList.size()];
				tableList.toArray(tables);
			}

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {

										// List<Object> whereInColumnValueList =
										// null;
										// for (Object whereArgValueListItem :
										// (List)whereArgValue) {
										// if (null != whereArgValueListItem) {
										// if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// {
										// if (null == whereInColumnValueList) {
										// whereInColumnValueList = new
										// ArrayList<Object>();
										// } // if (null ==
										// whereInColumnValueList)
										// whereInColumnValueList.add(whereArgValueListItem);
										// } // if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// } // if (null !=
										// whereArgValueListItem)
										// } // for (Object
										// whereArgValueListItem :
										// (List)whereArgValue)
										//
										// if (null != whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty()) {
										// whereInColumnList.add(whereArgKey);
										// whereParamMap.put(whereArgKey,
										// whereInColumnValueList);
										// } // if (null !=
										// whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty())

										whereInColumnList.add(whereArgKey);
										whereParamMap.put(whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())
					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			final String sql = SQLUtil.buildCountSqlStr(tables, table, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause);

			rowTotalCount = queryForInt(sql, paramMap);
		} // if ((null != table && 0 != table.trim().length()) || (null !=
			// tableList && 0 != tableList.size()))

		return rowTotalCount;
	}

	protected final int count(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupBy = groupArg;
					} else {
						groupBy.concat(", ").concat(groupArg);
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)
		} // if (null != groupArgList && !groupArgList.isEmpty())

		return count(sql, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause);
	}

	protected final int count(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause, Map<String, Object> outParaMap) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupBy = groupArg;
					} else {
						groupBy.concat(", ").concat(groupArg);
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)
		} // if (null != groupArgList && !groupArgList.isEmpty())

		return count(sql, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause, outParaMap);
	}

	private final int count(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause) {
		int rowTotalCount = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();

		String[] whereColumns = null;
		String[] whereInColumns = null;
		String[] whereLikeColumns = null;
		String[] whereRangeColumns = null;
		if (SQLUtil.isEmpty(whereClause)) {

			if (null != whereArgMap && !whereArgMap.isEmpty()) {

				final List<String> whereColumnList = new ArrayList<String>();
				final List<String> whereInColumnList = new ArrayList<String>();
				final Map<String, Object> whereParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
					if (null != whereArgEntry) {
						String whereArgKey = whereArgEntry.getKey();
						Object whereArgValue = whereArgEntry.getValue();
						if (null != whereArgKey && null != whereArgValue) {
							if (whereArgValue instanceof List) {
								if (!((List) whereArgValue).isEmpty()) {
									whereInColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								} // if (!((List)whereArgValue).isEmpty())
							} else {
								whereColumnList.add(whereArgKey);
								whereParamMap.put(whereArgKey, whereArgValue);
							}
						} // if (null != whereArgKey && null != whereArgValue)
					} // if (null != whereArgEntry)

				} // for (Map.Entry<String, Object> whereArgEntry :
					// whereArgMap.entrySet())

				if (!whereColumnList.isEmpty()) {
					whereColumns = new String[whereColumnList.size()];
					whereColumnList.toArray(whereColumns);
				} // if (!whereColumnList.isEmpty())

				if (!whereInColumnList.isEmpty()) {
					whereInColumns = new String[whereInColumnList.size()];
					whereInColumnList.toArray(whereInColumns);
				} // if (!whereInColumnList.isEmpty())

				paramMap.putAll(whereParamMap);
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			if (null != likeArgMap && !likeArgMap.isEmpty()) {

				final List<String> whereLikeColumnList = new ArrayList<String>();
				final Map<String, Object> likeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

					String likeArgKey = likeArgEntry.getKey();
					Object likeArgValue = likeArgEntry.getValue();
					if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
						String likeArgValueString = (String) likeArgValue;

						boolean hasWildcardBefore = false;
						boolean hasWildcardAfter = false;

						if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardBefore = true;
							likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardAfter = true;
							likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (!hasWildcardBefore && !hasWildcardAfter) {
							likeArgValueString = "%" + likeArgValueString + "%";
						} // if (!hasWildcardBefore && !hasWildcardAfter)

						if (hasWildcardBefore) {
							likeArgValueString = "%" + likeArgValueString;
						} // if (hasWildcardBefore)

						if (hasWildcardAfter) {
							likeArgValueString = likeArgValueString + "%";
						} // if (hasWildcardAfter)

						whereLikeColumnList.add(likeArgKey);
						likeParamMap.put(likeArgKey, likeArgValueString);

					} // if (null != likeArgKey && null != likeValueObject &&
						// likeValueObject instanceof String)
				} // for (Map.Entry<String, Object> likeArgEntry :
					// likeArgMap.entrySet())

				if (!whereLikeColumnList.isEmpty()) {
					whereLikeColumns = new String[whereLikeColumnList.size()];
					whereLikeColumnList.toArray(whereLikeColumns);
				} // if (!whereColumnList.isEmpty())

				paramMap.putAll(likeParamMap);
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				final List<String> whereRangeColumnList = new ArrayList<String>();
				final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
					final String rangeArgKey = rangeArgEntry.getKey();
					final Object rangeArgValue = rangeArgEntry.getValue();
					if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

						Object floorValueObject = ((List) rangeArgValue).get(0);
						Object ceilingValueObject = ((List) rangeArgValue).get(1);
						if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
							if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
								whereRangeColumnList.add(rangeArgEntry.getKey());

								whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
								whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
							} // if (!(floorValueObject instanceof String) ||
								// (!StringUtils.isEmpty(floorValueObject) &&
								// !StringUtils.isEmpty(ceilingValueObject)))
						} // if (null != floorValueObject && null !=
							// ceilingValueObject)

					} // if (null != rangeArgKey && null != rangeArgValue &&
						// rangeArgValue instanceof List && 2 ==
						// ((List)rangeArgValue).size())

				} // for (Map.Entry<String, Object> rangeArgEntry :
					// rangeArgMap.entrySet())

				if (!whereRangeColumnList.isEmpty()) {
					whereRangeColumns = new String[whereRangeColumnList.size()];
					whereRangeColumnList.toArray(whereRangeColumns);
				} // if (!whereRangeColumnList.isEmpty())

				paramMap.putAll(whereRangeParamMap);
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

		} // if (SQLUtil.isEmpty(whereClause))

		final String sqlStr = SQLUtil.buildCountSqlStr(sql, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause);

		rowTotalCount = queryForInt(sqlStr, paramMap);
		return rowTotalCount;
	}

	private final int count(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause, Map<String, Object> outParamMap) {
		int rowTotalCount = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();

		if (null != outParamMap) {
			paramMap.putAll(outParamMap);
		}

		String[] whereColumns = null;
		String[] whereInColumns = null;
		String[] whereLikeColumns = null;
		String[] whereRangeColumns = null;
		if (SQLUtil.isEmpty(whereClause)) {

			if (null != whereArgMap && !whereArgMap.isEmpty()) {

				final List<String> whereColumnList = new ArrayList<String>();
				final List<String> whereInColumnList = new ArrayList<String>();
				final Map<String, Object> whereParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
					if (null != whereArgEntry) {
						String whereArgKey = whereArgEntry.getKey();
						Object whereArgValue = whereArgEntry.getValue();
						if (null != whereArgKey && null != whereArgValue) {
							if (whereArgValue instanceof List) {
								if (!((List) whereArgValue).isEmpty()) {
									whereInColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								} // if (!((List)whereArgValue).isEmpty())
							} else {
								whereColumnList.add(whereArgKey);
								whereParamMap.put(whereArgKey, whereArgValue);
							}
						} // if (null != whereArgKey && null != whereArgValue)
					} // if (null != whereArgEntry)

				} // for (Map.Entry<String, Object> whereArgEntry :
					// whereArgMap.entrySet())

				if (!whereColumnList.isEmpty()) {
					whereColumns = new String[whereColumnList.size()];
					whereColumnList.toArray(whereColumns);
				} // if (!whereColumnList.isEmpty())

				if (!whereInColumnList.isEmpty()) {
					whereInColumns = new String[whereInColumnList.size()];
					whereInColumnList.toArray(whereInColumns);
				} // if (!whereInColumnList.isEmpty())

				paramMap.putAll(whereParamMap);
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			if (null != likeArgMap && !likeArgMap.isEmpty()) {

				final List<String> whereLikeColumnList = new ArrayList<String>();
				final Map<String, Object> likeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

					String likeArgKey = likeArgEntry.getKey();
					Object likeArgValue = likeArgEntry.getValue();
					if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
						String likeArgValueString = (String) likeArgValue;

						boolean hasWildcardBefore = false;
						boolean hasWildcardAfter = false;

						if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardBefore = true;
							likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardAfter = true;
							likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (!hasWildcardBefore && !hasWildcardAfter) {
							likeArgValueString = "%" + likeArgValueString + "%";
						} // if (!hasWildcardBefore && !hasWildcardAfter)

						if (hasWildcardBefore) {
							likeArgValueString = "%" + likeArgValueString;
						} // if (hasWildcardBefore)

						if (hasWildcardAfter) {
							likeArgValueString = likeArgValueString + "%";
						} // if (hasWildcardAfter)

						whereLikeColumnList.add(likeArgKey);
						likeParamMap.put(likeArgKey, likeArgValueString);

					} // if (null != likeArgKey && null != likeValueObject &&
						// likeValueObject instanceof String)
				} // for (Map.Entry<String, Object> likeArgEntry :
					// likeArgMap.entrySet())

				if (!whereLikeColumnList.isEmpty()) {
					whereLikeColumns = new String[whereLikeColumnList.size()];
					whereLikeColumnList.toArray(whereLikeColumns);
				} // if (!whereColumnList.isEmpty())

				paramMap.putAll(likeParamMap);
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				final List<String> whereRangeColumnList = new ArrayList<String>();
				final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
					final String rangeArgKey = rangeArgEntry.getKey();
					final Object rangeArgValue = rangeArgEntry.getValue();
					if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

						Object floorValueObject = ((List) rangeArgValue).get(0);
						Object ceilingValueObject = ((List) rangeArgValue).get(1);
						if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
							if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
								whereRangeColumnList.add(rangeArgEntry.getKey());

								whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
								whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
							} // if (!(floorValueObject instanceof String) ||
								// (!StringUtils.isEmpty(floorValueObject) &&
								// !StringUtils.isEmpty(ceilingValueObject)))
						} // if (null != floorValueObject && null !=
							// ceilingValueObject)

					} // if (null != rangeArgKey && null != rangeArgValue &&
						// rangeArgValue instanceof List && 2 ==
						// ((List)rangeArgValue).size())

				} // for (Map.Entry<String, Object> rangeArgEntry :
					// rangeArgMap.entrySet())

				if (!whereRangeColumnList.isEmpty()) {
					whereRangeColumns = new String[whereRangeColumnList.size()];
					whereRangeColumnList.toArray(whereRangeColumns);
				} // if (!whereRangeColumnList.isEmpty())

				paramMap.putAll(whereRangeParamMap);
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

		} // if (SQLUtil.isEmpty(whereClause))

		final String sqlStr = SQLUtil.buildCountSqlStr(sql, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause);

		rowTotalCount = queryForInt(sqlStr, paramMap);
		return rowTotalCount;
	}

	// private final long insert(final String sql, final Map<String, Object>
	// paramMap) {
	// long insertId = 0;
	//
	// if (null != namedParameterJdbcTemplate && !StringUtils.isEmpty(sql) &&
	// null != paramMap) {
	// try {
	//
	// final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
	// namedParameterJdbcTemplate.update(sql, new
	// MapSqlParameterSource(paramMap), generatedKeyHolder);
	//
	// final Number generatedKey = generatedKeyHolder.getKey();
	// if (null != generatedKey) {
	// insertId = generatedKey.longValue();
	// } // if (null != generatedKey)
	//
	// } catch (DataAccessException e) {
	// LOGGER.error(e.getMessage(), e);
	// }
	// } // if (null != namedParameterJdbcTemplate && !StringUtils.isEmpty(sql)
	// && null != paramMap)
	//
	// return insertId;
	// }

	protected final int update(final String sql, final Map<String, Object> paramMap) {
		int affectedRowsCount = 0;

		if (null != namedParameterJdbcTemplate && !StringUtils.isEmpty(sql) && null != paramMap) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);

				affectedRowsCount = namedParameterJdbcTemplate.update(sql, paramMap);
				log.debug("affectedRowsCount = " + affectedRowsCount);
			} catch (DataAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} // if (null != namedParameterJdbcTemplate && !StringUtils.isEmpty(sql)
			// && null != paramMap)

		return affectedRowsCount;
	}

	protected final long insertSql(final String sql, final Map<String, Object> paramMap) {
		long insertId = 0;
		if (null != namedParameterJdbcTemplate && !StringUtils.isEmpty(sql) && null != paramMap) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);
				final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
				namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), generatedKeyHolder);
				final Number generatedKey = generatedKeyHolder.getKey();
				if (null != generatedKey) {
					insertId = generatedKey.longValue();
					log.debug("insertId = " + insertId);
				}
			} catch (DataAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		return insertId;
	}

	protected final List<Map<String, Object>> queryForList(String sql) {
		return queryForList(sql, null);
	}

	public final List<Map<String, Object>> queryForList(final String sql, final Map<String, Object> paramMap) {
		List<Map<String, Object>> rowMapList = null;

		if (null != namedParameterJdbcTemplate) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);

				rowMapList = namedParameterJdbcTemplate.queryForList(sql, paramMap);
			} catch (DataAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} // if (null != namedParameterJdbcTemplate)

		return rowMapList;
	}

	@SuppressWarnings("unchecked")
	protected final <T> List<T> queryForList(final String sql, final Map<String, Object> paramMap, final Class<T> elementType) {
		List<T> rowList = null;

		if (null != namedParameterJdbcTemplate) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);
				log.debug("clazz = " + elementType);

				rowList = namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper(elementType));
				// namedParameterJdbcTemplate.getJdbcOperations().queryForList(sql,
				// Admin.class);

				log.debug("rowList = " + rowList);
			} catch (DataAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} // if (null != namedParameterJdbcTemplate)

		return rowList;
	}

	protected final int queryForInt(final String sql, final Map<String, Object> paramMap) {
		int resultNumber = 0;

		if (null != namedParameterJdbcTemplate) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);

				resultNumber = namedParameterJdbcTemplate.queryForInt(sql, paramMap);

				log.debug("resultNumber = " + resultNumber);
			} catch (EmptyResultDataAccessException e) {
			} catch (DataAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} // if (null != namedParameterJdbcTemplate)

		return resultNumber;
	}

	protected final Object queryForObject(final String sql, final Map<String, Object> paramMap, Class clazz) {
		Object result = 0;

		if (null != namedParameterJdbcTemplate) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);

				result = namedParameterJdbcTemplate.queryForObject(sql, paramMap, clazz);

				log.debug("result = " + result);
			} catch (DataAccessException e) {
				// LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} // if (null != namedParameterJdbcTemplate)

		return result;
	}

	protected final Map<String, Object> queryForMap(final String sql, final Map<String, Object> paramMap) {
		Map<String, Object> result = null;

		if (null != namedParameterJdbcTemplate) {
			try {
				log.debug("sql = " + sql);
				log.debug("paramMap = " + paramMap);

				result = namedParameterJdbcTemplate.queryForMap(sql, paramMap);

				log.debug("result = " + result);
			} catch (EmptyResultDataAccessException ae) {
				// do nothing 空数据集
				throw new RuntimeException(ae);
			} catch (DataAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} // if (null != namedParameterJdbcTemplate)

		return result;
	}

	protected final BigDecimal sum(final String sumField, final List<String> tableList, final String table, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupBy = groupArg;
					} else {
						groupBy.concat(", ").concat(groupArg);
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)
		} // if (null != groupArgList && !groupArgList.isEmpty())

		return sum(sumField, tableList, table, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause);
	}

	private final BigDecimal sum(final String sumField, final List<String> tableList, final String table, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause) {
		BigDecimal fieldTotalSum = BigDecimal.ZERO;
		if ((null != table && 0 != table.trim().length()) || (null != tableList && 0 != tableList.size())) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			String[] tables = null;
			if (null != tableList && 0 != tableList.size()) {
				tables = new String[tableList.size()];
				tableList.toArray(tables);
			}

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {

										// List<Object> whereInColumnValueList =
										// null;
										// for (Object whereArgValueListItem :
										// (List)whereArgValue) {
										// if (null != whereArgValueListItem) {
										// if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// {
										// if (null == whereInColumnValueList) {
										// whereInColumnValueList = new
										// ArrayList<Object>();
										// } // if (null ==
										// whereInColumnValueList)
										// whereInColumnValueList.add(whereArgValueListItem);
										// } // if (!(whereArgValueListItem
										// instanceof String) ||
										// !StringUtils.isEmpty(whereArgValueListItem))
										// } // if (null !=
										// whereArgValueListItem)
										// } // for (Object
										// whereArgValueListItem :
										// (List)whereArgValue)
										//
										// if (null != whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty()) {
										// whereInColumnList.add(whereArgKey);
										// whereParamMap.put(whereArgKey,
										// whereInColumnValueList);
										// } // if (null !=
										// whereInColumnValueList &&
										// !whereInColumnValueList.isEmpty())

										whereInColumnList.add(whereArgKey);
										whereParamMap.put(whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())

					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			final String sql = SQLUtil.buildSumSqlStr(sumField, tables, table, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause);
			log.debug("sql = " + sql);
			log.debug("paramMap = " + paramMap);
			try {
				Object fieldTotalSumResult = namedParameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);
				fieldTotalSum = GetAppUtil.getObject2BigDecimal(fieldTotalSumResult);
				log.debug("result = " + fieldTotalSumResult);
			} catch (NullPointerException e) {
				fieldTotalSum = BigDecimal.ZERO;
			}
		} // if ((null != table && 0 != table.trim().length()) || (null !=
			// tableList && 0 != tableList.size()))

		return fieldTotalSum;
	}

	protected final Map<String, Object> sum(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupBy = groupArg;
					} else {
						groupBy.concat(", ").concat(groupArg);
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)
		} // if (null != groupArgList && !groupArgList.isEmpty())

		return sum(sql, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause);
	}

	private final Map<String, Object> sum(StringBuilder sql, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause) {
		Map<String, Object> fieldTotalSum = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();

		String[] whereColumns = null;
		String[] whereInColumns = null;
		String[] whereLikeColumns = null;
		String[] whereRangeColumns = null;
		if (SQLUtil.isEmpty(whereClause)) {

			if (null != whereArgMap && !whereArgMap.isEmpty()) {

				final List<String> whereColumnList = new ArrayList<String>();
				final List<String> whereInColumnList = new ArrayList<String>();
				final Map<String, Object> whereParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
					if (null != whereArgEntry) {
						String whereArgKey = whereArgEntry.getKey();
						Object whereArgValue = whereArgEntry.getValue();
						if (null != whereArgKey && null != whereArgValue) {
							if (whereArgValue instanceof List) {
								if (!((List) whereArgValue).isEmpty()) {
									whereInColumnList.add(whereArgKey);
									whereParamMap.put(whereArgKey, whereArgValue);
								} // if (!((List)whereArgValue).isEmpty())
							} else {
								whereColumnList.add(whereArgKey);
								whereParamMap.put(whereArgKey, whereArgValue);
							}
						} // if (null != whereArgKey && null != whereArgValue)
					} // if (null != whereArgEntry)

				} // for (Map.Entry<String, Object> whereArgEntry :
					// whereArgMap.entrySet())

				if (!whereColumnList.isEmpty()) {
					whereColumns = new String[whereColumnList.size()];
					whereColumnList.toArray(whereColumns);
				} // if (!whereColumnList.isEmpty())

				if (!whereInColumnList.isEmpty()) {
					whereInColumns = new String[whereInColumnList.size()];
					whereInColumnList.toArray(whereInColumns);
				} // if (!whereInColumnList.isEmpty())

				paramMap.putAll(whereParamMap);
			} // if (null != whereArgMap && !whereArgMap.isEmpty())

			if (null != likeArgMap && !likeArgMap.isEmpty()) {

				final List<String> whereLikeColumnList = new ArrayList<String>();
				final Map<String, Object> likeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

					String likeArgKey = likeArgEntry.getKey();
					Object likeArgValue = likeArgEntry.getValue();
					if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
						String likeArgValueString = (String) likeArgValue;

						boolean hasWildcardBefore = false;
						boolean hasWildcardAfter = false;

						if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardBefore = true;
							likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
							hasWildcardAfter = true;
							likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
						} // if
							// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

						if (!hasWildcardBefore && !hasWildcardAfter) {
							likeArgValueString = "%" + likeArgValueString + "%";
						} // if (!hasWildcardBefore && !hasWildcardAfter)

						if (hasWildcardBefore) {
							likeArgValueString = "%" + likeArgValueString;
						} // if (hasWildcardBefore)

						if (hasWildcardAfter) {
							likeArgValueString = likeArgValueString + "%";
						} // if (hasWildcardAfter)

						whereLikeColumnList.add(likeArgKey);
						likeParamMap.put(likeArgKey, likeArgValueString);

					} // if (null != likeArgKey && null != likeValueObject &&
						// likeValueObject instanceof String)
				} // for (Map.Entry<String, Object> likeArgEntry :
					// likeArgMap.entrySet())

				if (!whereLikeColumnList.isEmpty()) {
					whereLikeColumns = new String[whereLikeColumnList.size()];
					whereLikeColumnList.toArray(whereLikeColumns);
				} // if (!whereColumnList.isEmpty())

				paramMap.putAll(likeParamMap);
			} // if (null != likeArgMap && !likeArgMap.isEmpty())

			if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
				final List<String> whereRangeColumnList = new ArrayList<String>();
				final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

				for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
					final String rangeArgKey = rangeArgEntry.getKey();
					final Object rangeArgValue = rangeArgEntry.getValue();
					if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

						Object floorValueObject = ((List) rangeArgValue).get(0);
						Object ceilingValueObject = ((List) rangeArgValue).get(1);
						if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
							if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
								whereRangeColumnList.add(rangeArgEntry.getKey());

								whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
								whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
							} // if (!(floorValueObject instanceof String) ||
								// (!StringUtils.isEmpty(floorValueObject) &&
								// !StringUtils.isEmpty(ceilingValueObject)))
						} // if (null != floorValueObject && null !=
							// ceilingValueObject)

					} // if (null != rangeArgKey && null != rangeArgValue &&
						// rangeArgValue instanceof List && 2 ==
						// ((List)rangeArgValue).size())

				} // for (Map.Entry<String, Object> rangeArgEntry :
					// rangeArgMap.entrySet())

				if (!whereRangeColumnList.isEmpty()) {
					whereRangeColumns = new String[whereRangeColumnList.size()];
					whereRangeColumnList.toArray(whereRangeColumns);
				} // if (!whereRangeColumnList.isEmpty())

				paramMap.putAll(whereRangeParamMap);
			} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

		} // if (SQLUtil.isEmpty(whereClause))

		final String sqlStr = SQLUtil.buildCountSqlStr(sql, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause);

		fieldTotalSum = queryForMap(sqlStr, paramMap);

		if (fieldTotalSum != null)
			return fieldTotalSum;
		else
			return new HashMap<String, Object>();
	}

	protected final List<Map<String, Object>> joinSelect(final boolean distinct, final String table, final List<String> columnList, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, final boolean isRandom, final int offset, final int pageSize, final String limit, final Map<String, String> joinActionMap, final Map<String, List<String>> joinColumnsMap, final Map<String, Map<String, String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			StringBuffer groupByStrBuf = new StringBuffer();

			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} else {
						groupByStrBuf.append(", ").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(groupBy));
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)

			if (0 != groupByStrBuf.length()) {
				groupBy = groupByStrBuf.toString();
			} // if (0 != groupByStrBuf.length())
		} // if (null != groupArgList && !groupArgList.isEmpty())

		String orderBy = null;
		if (isRandom) {
			orderBy = "rand()";
		} else if (null != sortArgMap && !sortArgMap.isEmpty()) {

			StringBuffer orderByStrBuf = new StringBuffer();

			for (Map.Entry<String, Object> sortArgEntry : sortArgMap.entrySet()) {

				String sortKey = sortArgEntry.getKey();
				Object sortValueObject = sortArgEntry.getValue();

				if (null != sortKey && null != sortValueObject && sortValueObject instanceof String) {
					if ("ASC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						if (sortKey.contains(".")) {
							String[] tmp = sortKey.split("\\.");
							orderByStrBuf.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1])).append(" ASC");
						} else
							orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" ASC");

					} else if ("DESC".equalsIgnoreCase((String) sortValueObject)) {
						if (0 != orderByStrBuf.length()) {
							orderByStrBuf.append(", ");
						} // if (0 != orderByStrBuf.length())

						if (sortKey.contains(".")) {
							String[] tmp = sortKey.split("\\.");
							orderByStrBuf.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1])).append(" DESC");
						} else
							orderByStrBuf.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(sortKey)).append(" DESC");

					}
				} // if (null != sortKey && null != sortValueObject &&
					// sortValueObject instanceof String)
			} // for (Map.Entry<String, Object> sortArgEntry :
				// sortArgMap.entrySet())

			if (0 != orderByStrBuf.length()) {
				orderBy = orderByStrBuf.toString();
			} // if (0 != orderByStrBuf.length())
		} else {
			orderBy = "created_at DESC";
		}

		return joinSelect(distinct, table, columnList, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit, joinActionMap, joinColumnsMap, joinOnArgsMap, joinWhereArgsMap);
	}

	private final List<Map<String, Object>> joinSelect(final boolean distinct, final String table, final List<String> columnList, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit, final Map<String, String> joinActionMap, final Map<String, List<String>> joinColumnsMap, final Map<String, Map<String, String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap) {

		List<Map<String, Object>> rowMapList = null;

		if (null != table && 0 != table.trim().length()) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			String[] columns = null;
			if (null != columnList && 0 != columnList.size()) {
				columns = new String[columnList.size()];
				columnList.toArray(columns);
			}

			Map<String, String[]> joinColumns = null;

			if (null != joinColumnsMap && 0 != joinColumnsMap.size()) {
				joinColumns = new HashMap<String, String[]>();
				for (Entry<String, List<String>> joinColumnsMapEntry : joinColumnsMap.entrySet()) {
					String key = joinColumnsMapEntry.getKey();
					List<String> vaule = joinColumnsMapEntry.getValue();
					if (null != key && null != vaule && vaule.size() > 0) {
						String[] tmp = new String[vaule.size()];
						vaule.toArray(tmp);
						joinColumns.put(key, tmp);
					}

				}
			}

			Map<String, String[]> joinOnArgs = null;
			if (null != joinOnArgsMap && 0 != joinOnArgsMap.size()) {
				joinOnArgs = new HashMap<String, String[]>();
				for (Entry<String, Map<String, String>> joinOnArgsMapEntry : joinOnArgsMap.entrySet()) {
					String key = joinOnArgsMapEntry.getKey();
					Map<String, String> vaule = joinOnArgsMapEntry.getValue();
					if (null != key && null != vaule && vaule.size() > 0) {
						String[] tmp = new String[vaule.size()];
						int i = 0;
						for (Entry<String, String> vauleEntry : vaule.entrySet()) {
							tmp[i] = key + "." + NamingUtil.convertPascalCase2LowerCaseWithUnderscore(vauleEntry.getKey()) + " = " + table + "." + NamingUtil.convertPascalCase2LowerCaseWithUnderscore(vauleEntry.getValue());
							i += 1;
						}
						joinOnArgs.put(key, tmp);
					}

				}
			}

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			String[] whereJoinColumns = null;
			String[] whereJoinInColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {
										whereInColumnList.add(table + "." + whereArgKey);
										whereParamMap.put(table + "." + whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(table + "." + whereArgKey);
									whereParamMap.put(table + "." + whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())

					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

				if (null != joinWhereArgsMap && !joinWhereArgsMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Entry<String, Map<String, Object>> joinWhereArgsEntry : joinWhereArgsMap.entrySet()) {
						if (null != joinWhereArgsEntry) {
							String joinTable = joinWhereArgsEntry.getKey();
							Map<String, Object> joinWhereValues = joinWhereArgsEntry.getValue();
							if (null != joinWhereValues && !joinWhereValues.isEmpty()) {
								for (Entry<String, Object> whereArgEntry : joinWhereValues.entrySet()) {
									String whereArgKey = whereArgEntry.getKey();
									Object whereArgValue = whereArgEntry.getValue();
									if (null != whereArgKey && null != whereArgValue) {
										if (whereArgValue instanceof List) {
											if (!((List) whereArgValue).isEmpty()) {
												whereInColumnList.add(joinTable + "." + whereArgKey);
												whereParamMap.put(joinTable + "." + whereArgKey, whereArgValue);
											}
										} else {
											whereColumnList.add(joinTable + "." + whereArgKey);
											whereParamMap.put(joinTable + "." + whereArgKey, whereArgValue);
										}
									}
								}
							}

						}

					}

					if (!whereColumnList.isEmpty()) {
						whereJoinColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereJoinColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereJoinInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereJoinInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != leftJoinWhereArgs &&
					// !leftJoinWhereArgs.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			whereColumns = (String[]) ArrayUtils.addAll(whereColumns, whereJoinColumns);
			whereInColumns = (String[]) ArrayUtils.addAll(whereInColumns, whereJoinInColumns);

			final String sql = SQLUtil.buildJoinSelectSqlStr(distinct, table, columns, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause, orderBy, offset, pageSize, limit, joinColumns, joinActionMap, joinOnArgs);

			rowMapList = queryForList(sql, paramMap);

		} // if ((null != table && 0 != table.trim().length()) || (null !=
			// tableList && 0 != tableList.size()))

		return rowMapList;
	}

	protected final int joinCount(final boolean distinct, final String table, final List<String> columnList, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final List<String> groupArgList, final String havingClause, final Map<String, Object> sortArgMap, final boolean isRandom, final int offset, final int pageSize, final String limit, final Map<String, String> joinActionMap, final Map<String, List<String>> joinColumnsMap, final Map<String, Map<String, String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap) {

		String groupBy = null;
		if (null != groupArgList && !groupArgList.isEmpty()) {
			for (String groupArg : groupArgList) {
				if (StringUtils.isEmpty(groupArg)) {
					if (StringUtils.isEmpty(groupBy)) {
						groupBy = groupArg;
					} else {
						groupBy.concat(", ").concat(groupArg);
					} // else
				} // if (StringUtils.isEmpty(groupArg))

			} // for (String groupArg : groupArgList)
		} // if (null != groupArgList && !groupArgList.isEmpty())

		return joinCount(distinct, table, columnList, whereArgMap, likeArgMap, rangeArgMap, whereClause, groupBy, havingClause, offset, pageSize, limit, joinActionMap, joinColumnsMap, joinOnArgsMap, joinWhereArgsMap);
	}

	private final int joinCount(final boolean distinct, final String table, final List<String> columnList, final Map<String, Object> whereArgMap, final Map<String, Object> likeArgMap, final Map<String, Object> rangeArgMap, final String whereClause, final String groupBy, final String havingClause, final int offset, final int pageSize, final String limit, final Map<String, String> joinActionMap, final Map<String, List<String>> joinColumnsMap, final Map<String, Map<String, String>> joinOnArgsMap, final Map<String, Map<String, Object>> joinWhereArgsMap) {

		int total = 0;

		if (null != table && 0 != table.trim().length()) {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			String[] columns = null;
			if (null != columnList && 0 != columnList.size()) {
				columns = new String[columnList.size()];
				columnList.toArray(columns);
			}

			Map<String, String[]> joinColumns = null;

			if (null != joinColumnsMap && 0 != joinColumnsMap.size()) {
				joinColumns = new HashMap<String, String[]>();
				for (Entry<String, List<String>> joinColumnsMapEntry : joinColumnsMap.entrySet()) {
					String key = joinColumnsMapEntry.getKey();
					List<String> vaule = joinColumnsMapEntry.getValue();
					if (null != key && null != vaule && vaule.size() > 0) {
						String[] tmp = new String[vaule.size()];
						vaule.toArray(tmp);
						joinColumns.put(key, tmp);
					}

				}
			}

			Map<String, String[]> joinOnArgs = null;
			if (null != joinOnArgsMap && 0 != joinOnArgsMap.size()) {
				joinOnArgs = new HashMap<String, String[]>();
				for (Entry<String, Map<String, String>> joinOnArgsMapEntry : joinOnArgsMap.entrySet()) {
					String key = joinOnArgsMapEntry.getKey();
					Map<String, String> vaule = joinOnArgsMapEntry.getValue();
					if (null != key && null != vaule && vaule.size() > 0) {
						String[] tmp = new String[vaule.size()];
						int i = 0;
						for (Entry<String, String> vauleEntry : vaule.entrySet()) {
							tmp[i] = key + "." + NamingUtil.convertPascalCase2LowerCaseWithUnderscore(vauleEntry.getKey()) + " = " + table + "." + NamingUtil.convertPascalCase2LowerCaseWithUnderscore(vauleEntry.getValue());
							i += 1;
						}
						joinOnArgs.put(key, tmp);
					}

				}
			}

			String[] whereColumns = null;
			String[] whereInColumns = null;
			String[] whereLikeColumns = null;
			String[] whereRangeColumns = null;
			String[] whereJoinColumns = null;
			String[] whereJoinInColumns = null;
			if (SQLUtil.isEmpty(whereClause)) {

				if (null != whereArgMap && !whereArgMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> whereArgEntry : whereArgMap.entrySet()) {
						if (null != whereArgEntry) {
							String whereArgKey = whereArgEntry.getKey();
							Object whereArgValue = whereArgEntry.getValue();
							if (null != whereArgKey && null != whereArgValue) {
								if (whereArgValue instanceof List) {
									if (!((List) whereArgValue).isEmpty()) {
										whereInColumnList.add(table + "." + whereArgKey);
										whereParamMap.put(table + "." + whereArgKey, whereArgValue);
									} // if (!((List)whereArgValue).isEmpty())
								} else {
									whereColumnList.add(table + "." + whereArgKey);
									whereParamMap.put(table + "." + whereArgKey, whereArgValue);
								}
							} // if (null != whereArgKey && null !=
								// whereArgValue)
						} // if (null != whereArgEntry)

					} // for (Map.Entry<String, Object> whereArgEntry :
						// whereArgMap.entrySet())

					if (!whereColumnList.isEmpty()) {
						whereColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != whereArgMap && !whereArgMap.isEmpty())

				if (null != likeArgMap && !likeArgMap.isEmpty()) {

					final List<String> whereLikeColumnList = new ArrayList<String>();
					final Map<String, Object> likeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> likeArgEntry : likeArgMap.entrySet()) {

						String likeArgKey = likeArgEntry.getKey();
						Object likeArgValue = likeArgEntry.getValue();
						if (null != likeArgKey && null != likeArgValue && likeArgValue instanceof String && !StringUtils.isEmpty(likeArgValue)) {
							String likeArgValueString = (String) likeArgValue;

							boolean hasWildcardBefore = false;
							boolean hasWildcardAfter = false;

							if (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardBefore = true;
								likeArgValueString = WILDCARD_BEFORE_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_BEFORE_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches()) {
								hasWildcardAfter = true;
								likeArgValueString = WILDCARD_AFTER_PATTERN.matcher(likeArgValueString).replaceAll("");
							} // if
								// (WILDCARD_AFTER_CHECK_PATTERN.matcher(likeArgValueString).matches())

							if (!hasWildcardBefore && !hasWildcardAfter) {
								likeArgValueString = "%" + likeArgValueString + "%";
							} // if (!hasWildcardBefore && !hasWildcardAfter)

							if (hasWildcardBefore) {
								likeArgValueString = "%" + likeArgValueString;
							} // if (hasWildcardBefore)

							if (hasWildcardAfter) {
								likeArgValueString = likeArgValueString + "%";
							} // if (hasWildcardAfter)

							whereLikeColumnList.add(likeArgKey);
							likeParamMap.put(likeArgKey, likeArgValueString);

						} // if (null != likeArgKey && null != likeValueObject
							// && likeValueObject instanceof String)
					} // for (Map.Entry<String, Object> likeArgEntry :
						// likeArgMap.entrySet())

					if (!whereLikeColumnList.isEmpty()) {
						whereLikeColumns = new String[whereLikeColumnList.size()];
						whereLikeColumnList.toArray(whereLikeColumns);
					} // if (!whereColumnList.isEmpty())

					paramMap.putAll(likeParamMap);
				} // if (null != likeArgMap && !likeArgMap.isEmpty())

				if (null != rangeArgMap && !rangeArgMap.isEmpty()) {
					final List<String> whereRangeColumnList = new ArrayList<String>();
					final Map<String, Object> whereRangeParamMap = new HashMap<String, Object>();

					for (Map.Entry<String, Object> rangeArgEntry : rangeArgMap.entrySet()) {
						final String rangeArgKey = rangeArgEntry.getKey();
						final Object rangeArgValue = rangeArgEntry.getValue();
						if (null != rangeArgKey && null != rangeArgValue && rangeArgValue instanceof List && 2 == ((List) rangeArgValue).size()) {

							Object floorValueObject = ((List) rangeArgValue).get(0);
							Object ceilingValueObject = ((List) rangeArgValue).get(1);
							if (null != floorValueObject && null != ceilingValueObject && floorValueObject.getClass() == ceilingValueObject.getClass()) {
								if (!(floorValueObject instanceof String) || (!StringUtils.isEmpty(floorValueObject) && !StringUtils.isEmpty(ceilingValueObject))) {
									whereRangeColumnList.add(rangeArgEntry.getKey());

									whereRangeParamMap.put(rangeArgEntry.getKey() + FLOOR_RANGE_SUFFIX, floorValueObject);
									whereRangeParamMap.put(rangeArgEntry.getKey() + CEILING_RANGE_SUFFIX, ceilingValueObject);
								} // if (!(floorValueObject instanceof String)
									// ||
									// (!StringUtils.isEmpty(floorValueObject)
									// &&
									// !StringUtils.isEmpty(ceilingValueObject)))
							} // if (null != floorValueObject && null !=
								// ceilingValueObject)

						} // if (null != rangeArgKey && null != rangeArgValue &&
							// rangeArgValue instanceof List && 2 ==
							// ((List)rangeArgValue).size())

					} // for (Map.Entry<String, Object> rangeArgEntry :
						// rangeArgMap.entrySet())

					if (!whereRangeColumnList.isEmpty()) {
						whereRangeColumns = new String[whereRangeColumnList.size()];
						whereRangeColumnList.toArray(whereRangeColumns);
					} // if (!whereRangeColumnList.isEmpty())

					paramMap.putAll(whereRangeParamMap);
				} // if (null != rangeArgMap && !rangeArgMap.isEmpty())

				if (null != joinWhereArgsMap && !joinWhereArgsMap.isEmpty()) {

					final List<String> whereColumnList = new ArrayList<String>();
					final List<String> whereInColumnList = new ArrayList<String>();
					final Map<String, Object> whereParamMap = new HashMap<String, Object>();

					for (Entry<String, Map<String, Object>> joinWhereArgsEntry : joinWhereArgsMap.entrySet()) {
						if (null != joinWhereArgsEntry) {
							String joinTable = joinWhereArgsEntry.getKey();
							Map<String, Object> joinWhereValues = joinWhereArgsEntry.getValue();
							if (null != joinWhereValues && !joinWhereValues.isEmpty()) {
								for (Entry<String, Object> whereArgEntry : joinWhereValues.entrySet()) {
									String whereArgKey = whereArgEntry.getKey();
									Object whereArgValue = whereArgEntry.getValue();
									if (null != whereArgKey && null != whereArgValue) {
										if (whereArgValue instanceof List) {
											if (!((List) whereArgValue).isEmpty()) {
												whereInColumnList.add(joinTable + "." + whereArgKey);
												whereParamMap.put(joinTable + "." + whereArgKey, whereArgValue);
											}
										} else {
											whereColumnList.add(joinTable + "." + whereArgKey);
											whereParamMap.put(joinTable + "." + whereArgKey, whereArgValue);
										}
									}
								}
							}

						}

					}

					if (!whereColumnList.isEmpty()) {
						whereJoinColumns = new String[whereColumnList.size()];
						whereColumnList.toArray(whereJoinColumns);
					} // if (!whereColumnList.isEmpty())

					if (!whereInColumnList.isEmpty()) {
						whereJoinInColumns = new String[whereInColumnList.size()];
						whereInColumnList.toArray(whereJoinInColumns);
					} // if (!whereInColumnList.isEmpty())

					paramMap.putAll(whereParamMap);
				} // if (null != leftJoinWhereArgs &&
					// !leftJoinWhereArgs.isEmpty())

			} // if (SQLUtil.isEmpty(whereClause))

			whereColumns = (String[]) ArrayUtils.addAll(whereColumns, whereJoinColumns);
			whereInColumns = (String[]) ArrayUtils.addAll(whereInColumns, whereJoinInColumns);
			;

			final String sql = SQLUtil.buildJoinCountSqlStr(distinct, table, columns, whereColumns, whereInColumns, whereLikeColumns, whereRangeColumns, whereClause, groupBy, havingClause, offset, pageSize, limit, joinColumns, joinActionMap, joinOnArgs);

			total = queryForInt(sql, paramMap);

		} // if ((null != table && 0 != table.trim().length()) || (null !=
			// tableList && 0 != tableList.size()))

		return total;
	}

	protected static List<Map<String, Object>> convertMap(List<Map<String, Object>> list) {
		List<Map<String, Object>> convertedList = null;
		if (null != list) {
			convertedList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : list) {
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

	protected static Map<String, Object> convertMap(Map<String, Object> map) {
		Map<String, Object> convertedMap = new HashMap<String, Object>();
		if (null != map) {
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
		}
		return convertedMap;
	}

	protected void batchInsert(String table, List<Map<String, Object>> list) {
		if (null != table && 0 != table.trim().length() && null != list && 0 != list.size()) {
			Map<String, Object> valueArgMap = list.get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			final int valueArgMapSize = valueArgMap.size();
			final String[] columns = new String[valueArgMapSize];
			// final Object[] values = new Object[valueArgMapSize];
			final List<String> columnList = new ArrayList<String>(valueArgMapSize);
			// final List<Object> valueList = new
			// ArrayList<Object>(valueArgMapSize);
			for (Map.Entry<String, Object> valueArgEntry : valueArgMap.entrySet()) {
				columnList.add(valueArgEntry.getKey());
				// valueList.add(valueArgEntry.getValue());
			}
			columnList.toArray(columns);
			final String sql = SQLUtil.buildInsertSqlStr(table, columns);

			int[] batchUpdate = namedParameterJdbcTemplate.batchUpdate(sql, list.toArray(new HashMap[list.size()]));
			log.info("====batchUpdate=======> " + Arrays.toString(batchUpdate));
		}
	}

	// TODO: 直接使用Bean
	// http://tianya23.blog.51cto.com/1081650/375823
	// http://my.oschina.net/happyBKs/blog/497798
	// http://blog.csdn.net/z69183787/article/details/40297253
}
