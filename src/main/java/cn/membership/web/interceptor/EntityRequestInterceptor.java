 
package cn.membership.web.interceptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体请求处理拦截器
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since       
 */
public class EntityRequestInterceptor extends DefaultRequestInterceptor {

    private Class<?> entityVOClass = null;

    private String entityVOClassName = null;

    @Override
    protected Map<String, Field> getRequestParamFieldMap() {
        Map<String, Field> requestParamFieldMap = super.getRequestParamFieldMap();

        if (null != entityVOClass) {
            Field[] entityVOClassFields = entityVOClass.getDeclaredFields();
            if (null != entityVOClassFields && 0 != entityVOClassFields.length) {
                if (null == requestParamFieldMap) {
                    requestParamFieldMap = new HashMap<String, Field>();
                } // if (null == requestParamFieldMap)

                for (Field field : entityVOClassFields) {
                    if (null != field) {
                        String fieldName = field.getName();
                        Class fieldType = field.getType();
                        if (null != fieldName && null != fieldType && Class.class != fieldType) {
//                            requestParamFieldMap.put(fieldName, fieldType);
                            requestParamFieldMap.put(fieldName, field);
                        } // if (null != fieldName && null != fieldType && Class.class != fieldType)

                    } // if (null != field)
                } // for (Field field : entityVOClassFields)
            } // if (null != entityVOClassFields)

        } // if (null != entityVOClass)

        return requestParamFieldMap;
    }

    public EntityRequestInterceptor(String entityVOClassName) {
        this.entityVOClassName = entityVOClassName;
        if (null != this.entityVOClassName) {
            try {
                this.entityVOClass = Class.forName(entityVOClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } // if (null != this.entityVOClassName)
    }
}
