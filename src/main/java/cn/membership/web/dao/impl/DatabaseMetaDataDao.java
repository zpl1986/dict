/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao.impl;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 表结构元数据DAO
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since       
 */

@Repository
@Qualifier("databaseMetaDataDao")
public class DatabaseMetaDataDao {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    //    private static Map<Type, List<FieldMetaData>> fieldMetaDataList = null;

    public String getTablename(Type type) {
        StringBuilder builder = new StringBuilder();
        if (null != type) {
            String className = StringUtils.getFilenameExtension(type.toString());
            builder.append(className.charAt(0));
            for (int i = 1; i < className.length(); i++) {
                char c = className.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    builder.append("_");
                }
                builder.append(c);
            }
        }
        return builder.toString().toLowerCase();
    }

    public class FieldMetaData {

        private String columnName;//COLUMN_NAME列名
        private int dataType; //DATA_TYPE,java.sql.Types(列的数据类型)
        private String typeName;//TYPE_NAME，跟dataType一对一
        private int columnSize;//COLUMN_SIZE，总长度
        private int decimalDigits;//DECIMAL_DIGITS，小数点位数
        private int numPrecRadix;//NUM_PREC_RADIX,10 or 2
        private int nullable;//NULLABLE,0false,1true
        private String remarks;//REMARKS，注释
        private String columnDef;//COLUMN_DEF，默认值
        private int charOctetLength;//CHAR_OCTET_LENGTH,字段最大长度
        private int ordinamPosition;//ORDINAL_POSITION,索引的位置index of column in table (starting at 1)
        private String isNullable;//IS_NULLABLE，"YES" or "NO"，与nullable对应
        private String scopeSchema;//SCOPE_SCHEMA
        private String scopeTable;//SCOPE_TABLE
        private short sourceDataType;//SOURCE_DATA_TYPE
        private String isAutoincrement;//IS_AUTOINCREMENT,"YES","NO"

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public int getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(int columnSize) {
            this.columnSize = columnSize;
        }

        public int getDecimalDigits() {
            return decimalDigits;
        }

        public void setDecimalDigits(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        public int getNumPrecRadix() {
            return numPrecRadix;
        }

        public void setNumPrecRadix(int numPrecRadix) {
            this.numPrecRadix = numPrecRadix;
        }

        public int getNullable() {
            return nullable;
        }

        public void setNullable(int nullable) {
            this.nullable = nullable;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getColumnDef() {
            return columnDef;
        }

        public void setColumnDef(String columnDef) {
            this.columnDef = columnDef;
        }

        public int getCharOctetLength() {
            return charOctetLength;
        }

        public void setCharOctetLength(int charOctetLength) {
            this.charOctetLength = charOctetLength;
        }

        public int getOrdinamPosition() {
            return ordinamPosition;
        }

        public void setOrdinamPosition(int ordinamPosition) {
            this.ordinamPosition = ordinamPosition;
        }

        public String getIsNullable() {
            return isNullable;
        }

        public void setIsNullable(String isNullable) {
            this.isNullable = isNullable;
        }

        public String getScopeSchema() {
            return scopeSchema;
        }

        public void setScopeSchema(String scopeSchema) {
            this.scopeSchema = scopeSchema;
        }

        public String getScopeTable() {
            return scopeTable;
        }

        public void setScopeTable(String scopeTable) {
            this.scopeTable = scopeTable;
        }

        public short getSourceDataType() {
            return sourceDataType;
        }

        public void setSourceDataType(short sourceDataType) {
            this.sourceDataType = sourceDataType;
        }

        public String getIsAutoincrement() {
            return isAutoincrement;
        }

        public void setIsAutoincrement(String isAutoincrement) {
            this.isAutoincrement = isAutoincrement;
        }

        @Override
        public String toString() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return "";
            }
        }
    }

    public List<FieldMetaData> getFieldMetaDataList(Type type) {

        //        if (null != fieldMetaDataList && null != fieldMetaDataList.get(type)) {
        //            return fieldMetaDataList.get(type);
        //        } 
        //        if (null == fieldMetaDataList) {
        //            fieldMetaDataList = new HashMap<>();
        //        } 

        List<FieldMetaData> fields = new ArrayList<FieldMetaData>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, getTablename(type), null);

            while (resultSet.next()) {
                FieldMetaData metaDataField = new FieldMetaData();
                metaDataField.setColumnName(resultSet.getString("COLUMN_NAME"));
                metaDataField.setDataType(resultSet.getInt("DATA_TYPE"));
                metaDataField.setTypeName(resultSet.getString("TYPE_NAME"));
                metaDataField.setColumnSize(resultSet.getInt("COLUMN_SIZE"));
                metaDataField.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
                metaDataField.setNumPrecRadix(resultSet.getInt("NUM_PREC_RADIX"));
                metaDataField.setNullable(resultSet.getInt("NULLABLE"));
                metaDataField.setRemarks(resultSet.getString("REMARKS"));
                metaDataField.setColumnDef(resultSet.getString("COLUMN_DEF"));
                metaDataField.setCharOctetLength(resultSet.getInt("CHAR_OCTET_LENGTH"));
                metaDataField.setOrdinamPosition(resultSet.getInt("ORDINAL_POSITION"));
                metaDataField.setIsNullable(resultSet.getString("IS_NULLABLE"));
                metaDataField.setScopeSchema(resultSet.getString("SCOPE_SCHEMA"));
                metaDataField.setScopeTable(resultSet.getString("SCOPE_TABLE"));
                metaDataField.setSourceDataType(resultSet.getShort("SOURCE_DATA_TYPE"));
                metaDataField.setIsAutoincrement(resultSet.getString("IS_AUTOINCREMENT"));
                fields.add(metaDataField);
            }
            //            fieldMetaDataList.put(type, fields);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return fields;
    }
}
