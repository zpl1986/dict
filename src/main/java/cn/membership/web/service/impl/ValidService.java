/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.service.impl;

import java.lang.reflect.Type;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.membership.common.util.NamingUtil;
import cn.membership.common.util.ObjectUtil;
import cn.membership.web.dao.impl.DatabaseMetaDataDao;
import cn.membership.web.dao.impl.DatabaseMetaDataDao.FieldMetaData;
import cn.membership.web.service.IValidService;

/**
* @author zhongpeiliang
* @version 0.0.1
* @since   
*/
@Service
@Qualifier("validService")
public class ValidService implements IValidService {

    @Autowired
    @Qualifier("databaseMetaDataDao")
    private DatabaseMetaDataDao databaseMetaDataDao;

    @Override
    public Map<String, Object> valid(Map<String, Object> parameterMap, Type type) {
        if (null == parameterMap) {
            parameterMap = new HashMap<String, Object>();
        }
        List<FieldMetaData> fieldMetaDataList = databaseMetaDataDao.getFieldMetaDataList(type);
        Map<String, Object> responseMap = new HashMap<>();

        int status = 0;
        String message = "成功！";

        for (FieldMetaData fieldMetaData : fieldMetaDataList) {
            String columnName = fieldMetaData.getColumnName();//字段名
            String columnNameCamelCase = NamingUtil.convertLowerCaseWithUnderscore2CamelCase(columnName);
            String remarks = fieldMetaData.getRemarks();//备注
            String columnDef = fieldMetaData.getColumnDef();//默认值
            String isAutoincrement = fieldMetaData.getIsAutoincrement();
            int columnSize = fieldMetaData.getColumnSize();
            int dataType = fieldMetaData.getDataType();
            Object object = parameterMap.get(columnNameCamelCase);

            //验证必填
            if (0 == fieldMetaData.getNullable() && null == columnDef && "NO".equalsIgnoreCase(isAutoincrement)) {
                if (null == object) {
                    status = 1;
                    message = remarks + "'" + columnNameCamelCase + "'必填！";
                    break;
                } else if (object instanceof Object[]) {
                    Object[] objects = (Object[]) object;
                    if (1 == objects.length && objects[0].toString().length() == 0) {
                        status = 1;
                        message = remarks + "'" + columnNameCamelCase + "'不能为空！";
                        break;
                    }
                }
            }

            if (null != object) {
                Collection<Object> collection = ObjectUtil.toCollection(object);
                for (Object obj : collection) {
                    if (dataType == Types.DATE || dataType == Types.TIMESTAMP || dataType == Types.TIME) { // DATE,DATETIME,TIME
                        String format = dataType == Types.DATE ? "yyyyMMdd" : dataType == Types.TIMESTAMP ? "yyyyMMddHHmmss" : dataType == Types.TIME ?"HHmmss":"";
                        if (obj instanceof Date) {
                            continue;
                        } else {
                            status = 1;
                            message = remarks + "'" + columnNameCamelCase + "'不符合格式" + format;
                            break;
                        }
                    }

                    // 验证参数是否超过最大长度
                    if (null != obj && obj.toString().length() > columnSize) {
                        status = 1;
                        message = remarks + "'" + columnNameCamelCase + "'超过了最长长度" + columnSize;
                        break;
                    }
                }
            }
        }

        responseMap.put("status", status);
        responseMap.put("message", message);
        return responseMap;
    }
}
