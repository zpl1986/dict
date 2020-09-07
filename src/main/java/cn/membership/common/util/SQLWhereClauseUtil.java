package cn.membership.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLWhereClauseUtil {

	public static String getWhereConditions(String associativeTable, int modeCode, Map<String, Object[]> equalArgMap,
			Map<String, Object[]> contentArgMap, Map<String, String[]> timeArgMap) {
		String where = "";
		String mode = "AND";
		if (0 == modeCode) {
			mode = "OR";
		}
		boolean hasAssociativeTable = false;
		if (null != associativeTable && 0 < associativeTable.length()) {
			hasAssociativeTable = true;
			where += associativeTable;
		}
		boolean isFirstFilter = true;
		if (hasAssociativeTable && ((null != equalArgMap && 0 < equalArgMap.size())
				|| (null != contentArgMap && 0 < contentArgMap.size())
				|| (null != timeArgMap && 0 < timeArgMap.size()))) {
			where += " AND (";
		}
		if (null != equalArgMap && 0 < equalArgMap.size()) {
			for (Map.Entry<String, Object[]> equalArgEntry : equalArgMap.entrySet()) {
				String key = equalArgEntry.getKey();
				Object[] equalFilters = equalArgEntry.getValue();
				if (!key.isEmpty() && null != equalFilters && 0 < equalFilters.length) {
					if (!isFirstFilter) {
						where += " " + mode + " ";
					}
					if (1 < equalFilters.length) {
						where += " (";
					}
					boolean isFirstItem = true;
					for (Object item : equalFilters) {
						if (!isFirstItem) {
							where += " OR ";
						}
						where += key + " = " + escape(item) + " ";
						isFirstItem = false;
					}
					if (1 < equalFilters.length) {
						where += ") ";
					}
					isFirstFilter = false;
				}
			}
		}
		if (null != contentArgMap && 0 < contentArgMap.size()) {
			for (Map.Entry<String, Object[]> contentArgEntry : contentArgMap.entrySet()) {
				String key = contentArgEntry.getKey();
				Object[] contentFilters = contentArgEntry.getValue();
				if (!key.isEmpty() && null != contentFilters && 0 < contentFilters.length) {
					if (!isFirstFilter) {
						where += " " + mode + " ";
					}
					boolean isFirstItem = true;
					if (1 < contentFilters.length) {
						where += " (";
					}
					for (Object item : contentFilters) {
						if (!isFirstItem) {
							where += " OR ";
						}
						where += key + " like \'%" + item + "%\'";
						isFirstItem = false;
					}
					if (1 < contentFilters.length) {
						where += ") ";
					}
					isFirstFilter = false;
				}
			}
		}
		if (null != timeArgMap && 0 < timeArgMap.size()) {
			for (Map.Entry<String, String[]> timeArgEntry : timeArgMap.entrySet()) {
				String key = timeArgEntry.getKey();
				String[] timeFilters = timeArgEntry.getValue();
				if (!key.isEmpty() && null != timeFilters && 2 == timeFilters.length) {
					if (!isFirstFilter) {
						where += " " + mode + " ";
					}
					if (!"".equals(timeFilters[0]) && !"".equals(timeFilters[1])) {
						if (0 > timeFilters[0].compareTo(timeFilters[1])) {
							where += " (" + key + " >= \'" + timeFilters[0] + "\' AND " + key + " <= \'"
									+ timeFilters[1] + "\') ";
						} else {
							where += " (" + key + " >= \'" + timeFilters[1] + "\' AND " + key + " <= \'"
									+ timeFilters[0] + "\') ";
						}
					} else if (!"".equals(timeFilters[0])) {
						where += " (" + key + " >= \'" + timeFilters[0] + "\') ";
					} else {
						where += " (" + key + " <= \'" + timeFilters[1] + "\') ";
					}
					isFirstFilter = false;
				}
			}
		}
		if (hasAssociativeTable && ((null != equalArgMap && 0 < equalArgMap.size())
				|| (null != contentArgMap && 0 < contentArgMap.size())
				|| (null != timeArgMap && 0 < timeArgMap.size()))) {
			where += " )";
		}
		return where;
	}

	private static String escape(Object obj) {
		String result = "";
		if (null != obj) {
			if (obj instanceof String) {
				result = "'" + obj.toString() + "'";
			} else {
				result = obj.toString();
			}
		} else {
			result = "null";
		}
		return result;
	}

	public static String getWhereConditionsByKeyword(String associativeTable, int modeCode, String[] equalsKeys,
			Object[] equalsValues, String[] likeKeys, Object[] likeValues) {
		String where = "";
		String mode = "AND";
		if (0 == modeCode) {
			mode = "OR";
		}

		if (null != associativeTable && 0 < associativeTable.length()) {
			where += associativeTable;
		}

		List<String> whereList = new ArrayList<String>();
		if (null != equalsKeys && equalsValues != null) {
			for (int i = 0; i < equalsValues.length; i++) {
				whereList.add(getSub(equalsKeys, "=", equalsValues[i]));
			}
		}

		if (null != likeKeys && null != likeValues) {
			for (int i = 0; i < likeValues.length; i++) {
				whereList.add(getSub(likeKeys, "like", likeValues[i]));
			}
		}

		String temp = "";
		String[] array = whereList.toArray(new String[] {});
		for (int i = 0; i < array.length; i++) {
			temp += " (" + array[i] + ") ";
			if (i != array.length - 1) {
				temp += mode;
			}
		}

		if (where.length() > 0) {
			where += " AND (" + temp + ")";
		} else {
			where = temp;
		}

		return where;
	}

	// name like '%李四%' OR age like '%李四%'
	private static String getSub(String keys[], String equalsOrLike, Object value) {
		String left = "";
		String right = "";
		if ("=".equals(equalsOrLike)) {
			left = " '";
			right = "' ";
		} else if ("like".equals(equalsOrLike)) {
			left = " '%";
			right = "%' ";
		} else {
			throw new RuntimeException("equalsOrLike只能是 = 或者like");
		}
		String retVal = "";

		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				retVal += keys[i] + " " + equalsOrLike + left + value + right;
				if (i != keys.length - 1) {
					retVal += " OR ";
				}
			}
		}
		return retVal;
	}

	public static void main(String[] args) {
		String associativeTable = " table1.name=table2.aname,tabe2.ss=table43.id ";
		// associativeTable = null;
		int modeCode = 0;
		// modeCode = 0;
		String aString[] = new String[] { "aaa", "bbb", "ccc" };

		Map<String, Object[]> equalArgMap = new HashMap<String, Object[]>();
		equalArgMap.put("name", aString);
		equalArgMap.put("age", aString);

		Map<String, Object[]> contentArgMap = new HashMap<String, Object[]>();
		contentArgMap.put("name", aString);
		contentArgMap.put("age", aString);

		Map<String, String[]> timeArgMap = new HashMap<String, String[]>();
		timeArgMap.put("aa", new String[] { "aa", "bb" });
		timeArgMap.put("aa2", new String[] { "aa", "bb" });

		String whereConditions = SQLWhereClauseUtil.getWhereConditions(associativeTable, modeCode, equalArgMap,
				contentArgMap, timeArgMap);
		System.out.println(whereConditions);
	}

}
