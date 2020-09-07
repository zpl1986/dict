package cn.membership.common.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class SQLUtil {

    private static final Pattern sLimitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");
    
    private static final String FLOOR_RANGE_SUFFIX = "Floor";
    private static final String CEILING_RANGE_SUFFIX = "Ceiling";

    public static String buildInsertSqlStr(final String table, final String[] columns) {
        if (null == columns || 0 == columns.length) {
            throw new IllegalArgumentException("Empty values");
        }

        StringBuilder sql = new StringBuilder(120);
        sql.append("INSERT INTO ");
        sql.append(table);

        sql.append(" (");
        appendColumns(sql, columns);
        sql.append(") VALUES (");
        appendColumns(sql, columns, ":");
        sql.append(")");

        return sql.toString();
    }

    public static String buildDeleteSqlStr(final String table, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, final String whereClause) {
        StringBuilder sql = new StringBuilder(120);
        sql.append("DELETE FROM ");

        sql.append(table);

        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);
            if (!isEmpty(whereSql)) {
                appendClause(sql, " WHERE ", whereSql.toString());
            }
        }

        return sql.toString();
    }

    public static String buildUpdateSqlStr(final String table, final String[] columns, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, final String whereClause) {
        if (null == columns || 0 == columns.length) {
            throw new IllegalArgumentException("Empty values");
        }

        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(table);

        sql.append(" SET ");
        appendColumnPairs(sql, columns);

        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);
            if (!isEmpty(whereSql)) {
                appendClause(sql, " WHERE ", whereSql.toString());
            }
        }

        return sql.toString();
    }

    public static String buildSelectSqlStr(final boolean distinct, final String[] tables, final String table, final String[] columns, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit) {

        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        if (!isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
        }

        StringBuilder sql = new StringBuilder(120);

        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        if (columns != null && columns.length != 0) {
            appendColumns(sql, columns);
        } else {
            sql.append("* ");
        }
        sql.append("FROM ");
        if (null != table && 0 != table.trim().length()) {
            sql.append(table);
        } else {
            appendColumns(sql, tables);
        }
        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);
            appendClause(sql, " WHERE ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);
        if(null != orderBy && orderBy.startsWith("name "))
            appendClause(sql, " ORDER BY ", "convert(name USING gbk) " + orderBy.substring(4));
        else
            appendClause(sql, " ORDER BY ", orderBy);
        if (!isEmpty(limit)) {
            appendClause(sql, " LIMIT ", limit);
        } else {
            StringBuilder limitSql = new StringBuilder(120);
            if (offset >= 0 && pageSize >= 0) {
                limitSql.append(offset);
                limitSql.append(", ");
                limitSql.append(pageSize);
            }
            if (!isEmpty(limitSql)) {
                appendClause(sql, " LIMIT ", limitSql.toString());
            }
        }

        return sql.toString();
    }
    
    public static String buildCountSqlStr(final String[] tables, final String table, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, final String whereClause, final String groupBy, final String havingClause) {
        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        
        StringBuilder sql = new StringBuilder(120);
        
        sql.append("SELECT COUNT(id) FROM ");
        if (null != table && 0 != table.trim().length()) {
            sql.append(table);
        } else {
            appendColumns(sql, tables);
        }
        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);
            appendClause(sql, " WHERE ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);
        
        return sql.toString();
    }


    public static String buildSumSqlStr(final String sumField,final String[] tables, final String table, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, final String whereClause, final String groupBy, final String havingClause) {
        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        if(null == sumField){
            throw new RuntimeException("sum字段不能为空");
        }
        String regex = "[a-z_]+";
        if(!Pattern.matches(regex, sumField)){
            throw new RuntimeException("字段名不正确:"+sumField);
        }
        
        StringBuilder sql = new StringBuilder(120);
        
        sql.append("SELECT sum("+sumField+") FROM ");
        if (null != table && 0 != table.trim().length()) {
            sql.append(table);
        } else {
            appendColumns(sql, tables);
        }
        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);
            appendClause(sql, " WHERE ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);
        
        return sql.toString();
    }
    
    public static String buildSelectSqlStr(StringBuilder sql, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, 
            final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit) {

        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        if (!isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
        }        
        
        if (!isEmpty(whereClause)) {
            appendClause(sql, "AND ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);            
            appendClause(sql, " AND ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);
        appendClause(sql, " ORDER BY ", orderBy);
        if (!isEmpty(limit)) {
            appendClause(sql, " LIMIT ", limit);
        } else {
            StringBuilder limitSql = new StringBuilder(120);
            if (offset >= 0 && pageSize >= 0) {
                limitSql.append(offset);
                limitSql.append(", ");
                limitSql.append(pageSize);
            }
            if (!isEmpty(limitSql)) {
                appendClause(sql, " LIMIT ", limitSql.toString());
            }
        }

        return sql.toString();
    }
    
    public static String buildCountSqlStr(StringBuilder sql, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, 
            final String whereClause, final String groupBy, final String havingClause) {

        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        
        if (!isEmpty(whereClause)) {
            appendClause(sql, " AND ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", null);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", null);            
            appendClause(sql, " AND ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);  

        return sql.toString();
    }
    
    public static String buildJoinSelectSqlStr(final boolean distinct, final String table, final String[] columns, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, 
            final String whereClause, final String groupBy, final String havingClause, final String orderBy, final int offset, final int pageSize, final String limit,
            final Map<String,String[]> joinColumns, final Map<String, String> joinAction, final Map<String,String[]> joinOnArgs) {

        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        if (!isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
        }

        StringBuilder sql = new StringBuilder(120);

        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        
        if (columns != null && columns.length != 0) {
            appendColumns(sql, columns, table + ".");
        } else {
            sql.append(table + ".* ");
        }
        
        if (joinColumns != null && joinColumns.size() > 0) { 
            for (Entry<String, String[]> entrySet : joinColumns.entrySet()){
                String key = entrySet.getKey();
                String[] value = entrySet.getValue();
                sql.append(",");
                appendColumns(sql, value , key + ".");   
            }
               
        } 
        
        sql.append("FROM ");
        if (null != table && 0 != table.trim().length()) {
            sql.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(table)).append(" ").append(table).append(" ");
        } 
        
        if (joinAction != null && joinAction.size() > 0 && joinOnArgs != null && joinOnArgs.size() > 0) {             
            
            for (Entry<String, String> entrySet : joinAction.entrySet()){
                String key = entrySet.getKey();
                String action = entrySet.getValue();
                if (action.trim().toLowerCase().endsWith("join")) {
                    sql.append(" ").append(action).append(" ").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(key)).append(" ").append(key).append(" ").append("on ");                     
                } else {
                    sql.append(" ").append(action).append(" ").append(key).append(" ").append("on ");   
                }
                appendColumns(sql, joinOnArgs.get(key) ," ");    
            } 
                  
        }        
        
        
        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", table);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", table);            
            appendClause(sql, " WHERE ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);
        appendClause(sql, " ORDER BY ", orderBy);
        if (!isEmpty(limit)) {
            appendClause(sql, " LIMIT ", limit);
        } else {
            StringBuilder limitSql = new StringBuilder(120);
            if (offset >= 0 && pageSize >= 0) {
                limitSql.append(offset);
                limitSql.append(", ");
                limitSql.append(pageSize);
            }
            if (!isEmpty(limitSql)) {
                appendClause(sql, " LIMIT ", limitSql.toString());
            }
        }

        return sql.toString();
    }
    
    public static String buildJoinCountSqlStr(final boolean distinct, final String table, final String[] columns, final String[] whereColumns, final String[] whereInColumns, final String[] whereLikeColumns, final String[] whereRangeColumns, 
            final String whereClause, final String groupBy, final String havingClause, final int offset, final int pageSize, final String limit,
            final Map<String,String[]> joinColumns, final Map<String, String> joinAction, final Map<String,String[]> joinOnArgs) {

        if (isEmpty(groupBy) && !isEmpty(havingClause)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        
        StringBuilder sql = new StringBuilder(120);        
        
        sql.append("SELECT COUNT(1) FROM ");
        
        if (null != table && 0 != table.trim().length()) {
            sql.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(table)).append(" ").append(table).append(" ");
        } 
        
        if (joinAction != null && joinAction.size() > 0 && joinOnArgs != null && joinOnArgs.size() > 0) {             
            
            for (Entry<String, String> entrySet : joinAction.entrySet()){
                String key = entrySet.getKey();
                String action = entrySet.getValue();
                if (action.trim().toLowerCase().endsWith("join")) {
                    sql.append(" ").append(action).append(" ").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(key)).append(" ").append(key).append(" ").append("on ");                     
                } else {
                    sql.append(" ").append(action).append(" ").append(key).append(" ").append("on ");   
                }
                appendColumns(sql, joinOnArgs.get(key) ," ");    
            } 
                  
        }        
        
        
        if (!isEmpty(whereClause)) {
            appendClause(sql, " WHERE ", whereClause);
        } else {
            StringBuilder whereSql = new StringBuilder(120);
            appendColumnPairs(whereSql, whereColumns, " AND ", null);
            appendInColumnPairs(whereSql, whereInColumns, " AND ", null);
            appendLikeColumnPairs(whereSql, whereLikeColumns, " AND ", table);
            appendRangeColumnPairs(whereSql, whereRangeColumns, " AND ", table);            
            appendClause(sql, " WHERE ", whereSql.toString());
        }
        appendClause(sql, " GROUP BY ", groupBy);
        appendClause(sql, " HAVING ", havingClause);  

        return sql.toString();
    }


    private static void appendColumns(final StringBuilder s, final String[] columns) {
        appendColumns(s, columns, null);
    }

    private static void appendColumns(final StringBuilder s, final String[] columns, final String prefix) {
        if (null != columns && columns.length > 0) {
            s.append(' ');
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];

                if (null != column) {
                    if (i > 0) {
                        s.append(", ");
                    }

                    if (null != prefix) {
                        s.append(prefix);
                    }

                    if (":".equals(prefix)) {
                        s.append(column);
                    } else if (" ".equals(prefix)) {
                        s.append(column);
                    } else {
                        s.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(column));
                    }
                }
            }
            s.append(' ');
        } // if (n > 0)
    }
   

    private static void appendColumnPairs(final StringBuilder s, final String[] columns) {
        appendColumnPairs(s, columns, ", ", null);
    }

    private static void appendColumnPairs(final StringBuilder s, final String[] columns, final String spliter, final String table) {
        if (null != columns && columns.length > 0) {
            
            s.append(' ');

            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];

                if (null != column) {
                    if (i > 0) {
                        s.append(spliter);
                    }
                    if (null != table && !"".equals(table)) {
                        s.append(" ").append(table).append(".");
                    }
                    if(column.contains(".")){
                        String[] tmp = column.split("\\.");
                        s.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1]));
                    }else
                        s.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(column));
                    s.append("=:");
                    s.append(column);
                }
            }
            s.append(' ');
        } // if (n > 0)
    }

    private static void appendInColumnPairs(final StringBuilder s, final String[] columns) {
        appendInColumnPairs(s, columns, " AND ", null);
    }

    private static void appendInColumnPairs(final StringBuilder s, final String[] columns, final String spliter, final String table) {
        if (null != columns && columns.length > 0) {

            if (!isEmpty(s)) {
                s.append(" AND ");
            } else {
                s.append(' ');
            }

            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];

                if (null != column) {
                    if (i > 0) {
                        s.append(spliter);
                    }
                    if (null != table && !"".equals(table)) {
                        s.append(" ").append(table).append(".");
                    }
                    if(column.contains(".")){
                        String[] tmp = column.split("\\.");
                        s.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1]));
                    }else
                        s.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(column));
                    s.append(" in(:");
                    s.append(column);
                    s.append(")");
                }
            }
            s.append(' ');
        } // if (n > 0)
    }
    
    private static void appendLikeColumnPairs(final StringBuilder s, final String[] columns) {
        appendLikeColumnPairs(s, columns, " AND ", null);
    }

    private static void appendLikeColumnPairs(final StringBuilder s, final String[] columns, final String spliter, final String table) {
        if (null != columns && columns.length > 0) {

            if (!isEmpty(s)) {
                s.append(" AND ");
            } else {
                s.append(' ');
            }

            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];

                if (null != column) {
                    if (i > 0) {
                        s.append(spliter);
                    }
                    if (null != table && !"".equals(table)) {
                        s.append(" ").append(table).append(".");
                    }
                    if(column.contains(".")){
                        String[] tmp = column.split("\\.");
                        s.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1]));
                    }else
                        s.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(column));
                    s.append(" LIKE :");
                    s.append(column);
                }
            }
            s.append(' ');
        } // if (n > 0)
    }
    
    private static void appendRangeColumnPairs(final StringBuilder s, final String[] columns) {
        appendRangeColumnPairs(s, columns, " AND ", null);
    }

    private static void appendRangeColumnPairs(final StringBuilder s, final String[] columns, final String spliter, final String table) {
        if (null != columns && columns.length > 0) {

            if (!isEmpty(s)) {
                s.append(" AND ");
            } else {
                s.append(' ');
            }

            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];

                if (null != column) {
                    if (i > 0) {
                        s.append(spliter);
                    }
                    s.append("(");
                    if (null != table && !"".equals(table)) {
                        s.append(" ").append(table).append(".");
                    }
                    if(column.contains(".")){
                        String[] tmp = column.split("\\.");
                        s.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1]));
                    }else
                        s.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(column));
                    s.append(" >= :");
                    s.append(column);
                    s.append(FLOOR_RANGE_SUFFIX);
                    s.append(" AND ");
                    if (null != table && !"".equals(table)) {
                        s.append(" ").append(table).append(".");
                    }
                    if(column.contains(".")){
                        String[] tmp = column.split("\\.");
                        s.append(tmp[0]).append(".").append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(tmp[1]));
                    }else
                        s.append(NamingUtil.convertPascalCase2LowerCaseWithUnderscore(column));
                    s.append(" <= :");
                    s.append(column);
                    s.append(CEILING_RANGE_SUFFIX);
                    s.append(")");
                }
            }
            s.append(' ');
        } // if (n > 0)
    }
    

    private static void appendClause(final StringBuilder s, final String name, final String clause) {
        appendClause(s, name, clause, null);
    }

    private static void appendClause(final StringBuilder s, final String name, final String clause, final String prefix) {
        if (!isEmpty(clause)) {
            s.append(name);
            if (null != prefix) {
                s.append(prefix);
            }
            s.append(clause);
        }
    }

    public static boolean isEmpty(final CharSequence str) {

        boolean isEmpty = false;

        if (str == null || str.length() == 0) {
            isEmpty = true;
        }

        return isEmpty;
    }

}
