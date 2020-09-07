 
package cn.membership.web.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.membership.common.util.ModelMapUtil;
import cn.membership.common.util.RequestAttrUtil;
import cn.membership.common.util.RequestParamUtil;
import cn.membership.web.constant.WebRequestConstant;
import cn.membership.web.service.IEntityService;
import cn.membership.web.service.IValidService;

/**
 * 实体控制器
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since 	
 */
public abstract class EntityController<T, TService> extends BaseController {

    @Autowired
    @Qualifier("validService")
    private IValidService validService;

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
                                entityVOFieldMap.put(field.getName(), field);
                            } // if (null != fieldName && null != fieldType && Class.class != fieldType)
                        } // if (null != field)
                    } // for (Field field : entityVOClassFields)
                } // if (null != entityVOClassFields)

            } // if (null != entityVOClass)
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return entityVOFieldMap;
    }

    protected String getEntityVOClassSimpleName() {
        String entityVOClassSimpleName = null;

        String entityVOClassName = getEntityVOClassName();
        if (null != entityVOClassName) {
            entityVOClassSimpleName = entityVOClassName.substring(entityVOClassName.lastIndexOf(".") + 1);
        } // if (null != entityVOClassName)

        return entityVOClassSimpleName;
    }

    protected String getEntityVOClassName() {
        String entityVOClassName = null;

        IEntityService entityService = getEntityService();
        if (null != entityService) {

            String entityVOClassNameFromService = entityService.getEntityVOClassName();
            if (null != entityVOClassNameFromService) {
                ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
                if (null != type) {
                    Type[] actualTypeArguments = type.getActualTypeArguments();
                    if (null != actualTypeArguments && actualTypeArguments.length > 0) {
                        entityVOClassName = actualTypeArguments[0].toString();
                        entityVOClassName = entityVOClassName.substring(entityVOClassName.lastIndexOf(" ") + 1);
                    } // if (null != actualTypeArguments && actualTypeArguments.length > 0)

                } // if (null != type)  

                if (!entityVOClassNameFromService.equals(entityVOClassName)) {
                    entityVOClassName = null;
                } // if (!entityVOClassNameFromService.equals(entityVOClassName))
            } // if (null != entityVOClassNameFromService)

        } // if (null != entityService)   

        return entityVOClassName;
    }

    protected String getEntityServiceClassSimpleName() {
        String entityServiceClassSimpleName = null;

        String entityDAOClassName = getEntityServiceClassName();
        if (null != entityDAOClassName) {
            entityServiceClassSimpleName = entityDAOClassName.substring(entityDAOClassName.lastIndexOf(".") + 1);
        } // if (null != entityDAOClassName)

        return entityServiceClassSimpleName;
    }

    protected String getEntityServiceClassName() {
        String entityServiceClassName = null;

        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        if (null != type) {
            Type[] actualTypeArguments = type.getActualTypeArguments();
            if (null != actualTypeArguments && actualTypeArguments.length > 1) {
                entityServiceClassName = actualTypeArguments[1].toString();
                entityServiceClassName = entityServiceClassName.substring(entityServiceClassName.lastIndexOf(" ") + 1);
            } // if (null != actualTypeArguments && actualTypeArguments.length > 0)

        } // if (null != type)        

        return entityServiceClassName;
    }

    protected IEntityService getEntityService() {

        IEntityService entityService = null;

        String entityServiceClassSimpleName = getEntityServiceClassSimpleName();
        if (null != entityServiceClassSimpleName) {
            try {
                Field field = getClass().getDeclaredField(entityServiceClassSimpleName.substring(0, 1).toLowerCase().concat(entityServiceClassSimpleName.substring(1)));
                if (null != field) {
                    field.setAccessible(true);
                    entityService = (IEntityService) field.get(this);
                } // if (null != field)
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if (null != entityDAOClassSimpleName)

        return entityService;
    }

    protected boolean validateCreate(HttpServletRequest request, ModelMap modelMap) {
        boolean isValid = true;
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);
            if (0 == status) { //验证请求参数
                ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
                if (null != parameterizedType) {
                    Type[] types = parameterizedType.getActualTypeArguments();
                    if (null != types && types.length >= 1) {
                        Map<String, Object> requestArgs = RequestParamUtil.getRequestArgs(request, modelMap);
                        Map<String, Object> validMap = validService.valid(requestArgs, types[0]);
                        if (null != validMap && validMap.get(WebRequestConstant.STATUS_MODEL_MAP_KEY) instanceof Integer && 0 != (Integer) validMap.get(WebRequestConstant.STATUS_MODEL_MAP_KEY)) {
                            status = (Integer) validMap.get(WebRequestConstant.STATUS_MODEL_MAP_KEY);
                            message = (String) validMap.get(WebRequestConstant.MESSAGE_MODEL_MAP_KEY);
                            isValid = false;
                        }
                    }
                }
            } else {
                isValid = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);

        return isValid;
    } // validateCreate

    protected void preCreate(HttpServletRequest request, ModelMap modelMap) {

    } // preCreate

    protected T handleCreate(HttpServletRequest request, ModelMap modelMap) {
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());

        T createdEntity = null;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                T entity = (T) Class.forName(getEntityVOClassName()).newInstance();
                if (null != entity) {

                    BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());
                    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                    if (null != propertyDescriptors && 0 != propertyDescriptors.length) {
                        Map<String, Object> requestArgs = RequestParamUtil.getRequestArgs(request, modelMap);
                        if (null != requestArgs) {
                            for (PropertyDescriptor property : propertyDescriptors) {
                                final String propertyName = property.getName();
                                if (null != propertyName) {
                                    
                                    Object propertyValue = requestArgs.get(propertyName);
                                    if (null != propertyValue && property.getPropertyType().isInstance(propertyValue)) {
                                        Method setter = property.getWriteMethod();
                                        setter.invoke(entity, propertyValue);
                                    } // if (null != propertyValue && property.getPropertyType().isInstance(propertyValue))
                                } // if (null != propertyName)

                            } // for (PropertyDescriptor property : propertyDescriptors)

                        } // if (null != requestArgs)
                    } // if (null != propertyDescriptors && 0 != propertyDescriptors.length)

                    IEntityService entityService = getEntityService();
                    if (null != entityService) {
                        final Long entityCreatedId = entityService.create(entity);
                        if (null != entityCreatedId && 0L != entityCreatedId) {
                            createdEntity = (T) entityService.fetchDetail(entityCreatedId);

                            String entityVOClassSimpleName = getEntityVOClassSimpleName();
                            result.put(entityVOClassSimpleName.substring(0, 1).toLowerCase().concat(entityVOClassSimpleName.substring(1)), createdEntity);
                        } else {
                            status = 1;
                            message = "失败";
                        }

                    } // if (null != entityService)   

                } // if (null != entity)
            } // if (0 == status)

        } catch (DuplicateKeyException e) {
            status = 1;
            message = "失败";
            String errorMessage = e.getMessage();//Duplicate entry '380007905@qq.com' for key
            String regex = "Duplicate entry '.*?' for key";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(errorMessage);
            if (matcher.find()) {
                String group = matcher.group();
                group = group.replace("Duplicate entry '", "").replace("' for key", "");
                message = group + "已被使用";
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE.concat("" + e.getLocalizedMessage());
        }
        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);

        return createdEntity;
    } // handleCreate

    protected void postCreate(HttpServletRequest request, ModelMap modelMap) {

    } // postCreate

    @RequestMapping(value = "/create", method = { RequestMethod.GET, RequestMethod.POST })
    public void create(HttpServletRequest request, ModelMap modelMap) {

        if (validateCreate(request, modelMap)) {
            preCreate(request, modelMap);
            handleCreate(request, modelMap);
            postCreate(request, modelMap);
        } // if (validateCreate(request, modelMap))
    } // create

    protected boolean validateRemove(HttpServletRequest request, ModelMap modelMap) {

        boolean isValid = true;
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                Map<String, Object> whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);

                if ((null == whereArgMap || whereArgMap.isEmpty()) && (null == likeArgMap || likeArgMap.isEmpty()) && (null == rangeArgMap || rangeArgMap.isEmpty())) {
                    isValid = false;
                    status = 1;
                    message = "缺少删除条件";
                } // if ((null == whereArgMap || whereArgMap.isEmpty()) && (null == likeArgMap || likeArgMap.isEmpty()) && (null == rangeArgMap || rangeArgMap.isEmpty()))
            } else {
                isValid = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);

        return isValid;
    } // validateRemove

    protected void preRemove(HttpServletRequest request, ModelMap modelMap) {

    } // preRemove

    protected int handleRemove(HttpServletRequest request, ModelMap modelMap) {
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());

        int affectedRowsCount = WebRequestConstant.DEFAULT_AFFECTED_ROWS_COUNT;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {

                Map<String, Object> whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);

                if ((null != whereArgMap && !whereArgMap.isEmpty()) || (null != likeArgMap && !likeArgMap.isEmpty()) || (null != rangeArgMap && !rangeArgMap.isEmpty())) {
                    IEntityService entityService = getEntityService();
                    if (null != entityService) {
                        affectedRowsCount = entityService.remove(whereArgMap, likeArgMap, rangeArgMap);
                    } // if (null != entityService)
                } // if ((null != whereArgMap && !whereArgMap.isEmpty()) || (null != likeArgMap && !likeArgMap.isEmpty()) || (null != rangeArgMap && !rangeArgMap.isEmpty()))

                result.put(WebRequestConstant.AFFECTED_ROWS_COUNT_RESULT_MAP_KEY, affectedRowsCount);
            } // if (0 == status)
        } catch (Exception e) {
            e.printStackTrace();
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);

        return affectedRowsCount;
    } // handleRemove

    protected void postRemove(HttpServletRequest request, ModelMap modelMap) {

    } // postRemove

    @RequestMapping(value = "/remove", method = { RequestMethod.GET, RequestMethod.POST })
    public void remove(HttpServletRequest request, ModelMap modelMap) {

        if (validateRemove(request, modelMap)) {
            preRemove(request, modelMap);
            handleRemove(request, modelMap);
            postRemove(request, modelMap);
        } // if (validateRemove(request, modelMap))
    } // remove    

    protected boolean validateUpdate(HttpServletRequest request, ModelMap modelMap) {
        boolean isValid = true;
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                Map<String, Object> valueArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.VALUE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                //                Map<String, Object> whereArgMap = new HashMap<String, Object>();
                //                if(null != valueArgMap){
                //                    whereArgMap.put("id", (Long) valueArgMap.get("id"));
                //                }
                Map<String, Object> whereArgMap = null;
                Map<String, Object> allArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                if (null != allArgMap && allArgMap.containsKey(WebRequestConstant.WHERE_FIELD_LIST_REQUEST_PARAM_KEY)) {
                    whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                } else {
                    if (null != valueArgMap && valueArgMap.containsKey("id")) {
                        whereArgMap = new HashMap<String, Object>();
                        whereArgMap.put("id", valueArgMap.remove("id"));
                    } // if (null != valueArgMap && valueArgMap.containsKey("id"))
                }
                Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);

                if ((null == whereArgMap || whereArgMap.isEmpty()) && (null == likeArgMap || likeArgMap.isEmpty()) && (null == rangeArgMap || rangeArgMap.isEmpty())) {
                    status = 1;
                    message = "未设置更新条件";
                } else if (null == valueArgMap || valueArgMap.isEmpty()) {
                    status = 1;
                    message = "没有更新项！";
                }
            } else {
                isValid = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);

        return isValid;
    } // validateUpdate

    protected void preUpdate(HttpServletRequest request, ModelMap modelMap) {

    } // preUpdate

    protected int handleUpdate(HttpServletRequest request, ModelMap modelMap) {
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());
        int affectedRowsCount = WebRequestConstant.DEFAULT_AFFECTED_ROWS_COUNT;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);
            if (0 == status) {

                Map<String, Object> valueArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.VALUE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                //                Map<String, Object> whereArgMap = new HashMap<String, Object>();
                //                whereArgMap.put("id", valueArgMap.remove("id"));
                Map<String, Object> whereArgMap = null;
                Map<String, Object> allArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                if (null != allArgMap && allArgMap.containsKey(WebRequestConstant.WHERE_FIELD_LIST_REQUEST_PARAM_KEY)) {
                    whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                } else {
                    if (null != valueArgMap && valueArgMap.containsKey("id")) {
                        whereArgMap = new HashMap<String, Object>();
                        whereArgMap.put("id", valueArgMap.remove("id"));
                    } // if (null != valueArgMap && valueArgMap.containsKey("id"))
                    else if(null != allArgMap && allArgMap.containsKey("id")){
                        whereArgMap = new HashMap<String, Object>();
                        whereArgMap.put("id", allArgMap.get("id"));
                    }
                }
                Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                if (null != valueArgMap && !valueArgMap.isEmpty() && ((null != whereArgMap && !whereArgMap.isEmpty()) || (null != likeArgMap && !likeArgMap.isEmpty()) || (null != rangeArgMap && !rangeArgMap.isEmpty()))) {
                    IEntityService entityService = getEntityService();
                    if (null != entityService) {
                        affectedRowsCount = entityService.update(valueArgMap, whereArgMap, likeArgMap, rangeArgMap);
                    } // if (null != entityService)                       
                } // if (null != valueArgMap && !valueArgMap.isEmpty() && ((null != whereArgMap && !whereArgMap.isEmpty()) || (null != likeArgMap && !likeArgMap.isEmpty()) || (null != rangeArgMap && !rangeArgMap.isEmpty())))

                result.put(WebRequestConstant.AFFECTED_ROWS_COUNT_RESULT_MAP_KEY, affectedRowsCount);
            } // if (0 == status)
        } catch (Exception e) {
            e.printStackTrace();
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);

        return affectedRowsCount;
    } // handleUpdate

    protected void postUpdate(HttpServletRequest request, ModelMap modelMap) {

    } // postUpdate

    @RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
    public void update(HttpServletRequest request, ModelMap modelMap) {

        if (validateUpdate(request, modelMap)) {
            preUpdate(request, modelMap);
            handleUpdate(request, modelMap);
            postUpdate(request, modelMap);
        } // if (validateUpdate(request, modelMap))
    } // update

    protected boolean validateFetch(HttpServletRequest request, ModelMap modelMap) {
        boolean isValid = true;
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {

            } else {
                isValid = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);

        return isValid;
    } // validateFetch

    protected void preFetch(HttpServletRequest request, ModelMap modelMap) {

    } // preFetch

    protected List<T> handleFetch(HttpServletRequest request, ModelMap modelMap) {
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());

        List<T> entityList = null;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                boolean random = RequestParamUtil.getRequestParamBoolean(WebRequestConstant.RANDOM_REQUEST_PARAM_KEY, request, modelMap, WebRequestConstant.DEFAULT_RANDOM);
                int offset = RequestParamUtil.getRequestParamInteger(WebRequestConstant.OFFSET_REQUEST_PARAM_KEY, request, modelMap, WebRequestConstant.DEFAULT_OFFSET);
                int pageSize = RequestParamUtil.getRequestParamInteger(WebRequestConstant.PAGE_SIZE_REQUEST_PARAM_KEY, request, modelMap, WebRequestConstant.DEFAULT_PAGE_SIZE);

                IEntityService entityService = getEntityService();
                if (null != entityService) {

                    Map<String, Object> whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                    Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                    Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                    //                    List<Object> groupArgList = RequestAttrUtil.getRequestAttrList(ControllerConstant.GROUP_ARG_LIST_REQUEST_ATTR_KEY, request, modelMap);
                    Map<String, Object> sortArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.SORT_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                    int rowTotolCount = entityService.count(whereArgMap, likeArgMap, rangeArgMap);
                    result.put(WebRequestConstant.TOTAL_RESULT_MAP_KEY, rowTotolCount);

                    entityList = entityService.fetch(random, offset, (Integer.MAX_VALUE != pageSize && pageSize > 0 ? pageSize + 1 : pageSize), whereArgMap, likeArgMap, rangeArgMap, sortArgMap);
                    if (null != entityList && !entityList.isEmpty()) {

                        boolean toBeContinued = false;
                        if (entityList.size() > pageSize) {
                            toBeContinued = true;
                            entityList = entityList.subList(0, pageSize);
                        } // if (entityList.size() > pageSize) 

                        result.put(WebRequestConstant.TO_BE_CONTINUED_RESULT_MAP_KEY, toBeContinued);

                        result.put(WebRequestConstant.FROM_RESULT_MAP_KEY, offset);
                        result.put(WebRequestConstant.TO_RESULT_MAP_KEY, offset + entityList.size() - 1);
                    } // if (null != entityList && !entityList.isEmpty())

                    String entityVOClassSimpleName = getEntityVOClassSimpleName();
                    result.put(entityVOClassSimpleName.substring(0, 1).toLowerCase().concat(entityVOClassSimpleName.substring(1)).concat("List"), entityList);

                } // if (null != entityService)    
            } // if (0 == status)
        } catch (Exception e) {
            e.printStackTrace();
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);

        return entityList;
    } // handleFetch

    protected void postFetch(HttpServletRequest request, ModelMap modelMap) {

    } // postFetch

    @RequestMapping(value = "/fetch", method = { RequestMethod.GET, RequestMethod.POST })
    public void fetch(HttpServletRequest request, ModelMap modelMap) {

        if (validateFetch(request, modelMap)) {
            preFetch(request, modelMap);
            handleFetch(request, modelMap);
            postFetch(request, modelMap);
        } // if (validateFetch(request, modelMap))
    } // fetch

    protected boolean validateCount(HttpServletRequest request, ModelMap modelMap) {
        boolean isValid = true;
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {

            } else {
                isValid = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);

        return isValid;
    } // validateCount

    protected void preCount(HttpServletRequest request, ModelMap modelMap) {

    } // preCount

    protected int handleCount(HttpServletRequest request, ModelMap modelMap) {
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());

        int totalCount = 0;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                IEntityService entityService = getEntityService();
                if (null != entityService) {

                    Map<String, Object> whereArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.WHERE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                    Map<String, Object> likeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.LIKE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
                    Map<String, Object> rangeArgMap = RequestAttrUtil.getRequestAttrMap(WebRequestConstant.RANGE_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);

                    totalCount = entityService.count(whereArgMap, likeArgMap, rangeArgMap);
                    result.put(WebRequestConstant.TOTAL_RESULT_MAP_KEY, totalCount);
                } // if (null != entityService)       
            } // if (0 == status)
        } catch (Exception e) {
            e.printStackTrace();
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);

        return totalCount;
    } // handleCount

    protected void postCount(HttpServletRequest request, ModelMap modelMap) {

    } // postCount

    @RequestMapping(value = "/count", method = { RequestMethod.GET, RequestMethod.POST })
    public void count(HttpServletRequest request, ModelMap modelMap) {

        if (validateCount(request, modelMap)) {
            preCount(request, modelMap);
            handleCount(request, modelMap);
            postCount(request, modelMap);
        } // if (validateCount(request, modelMap))
    } // count

    protected boolean validateFetchDetail(HttpServletRequest request, ModelMap modelMap) {

        boolean isValid = true;
        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                final long id = RequestParamUtil.getRequestParamLong("id", request, modelMap, 0L);
                if (0L == id) {
                    isValid = false;
                    status = 1;
                    message = "未传id，或者id无效！ ";
                } // if(0L == id)
            } else {
                isValid = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);

        return isValid;
    } // validateFetchDetail

    protected void preFetchDetail(HttpServletRequest request, ModelMap modelMap) {

    } // preFetchDetail

    protected T handleFetchDetail(HttpServletRequest request, ModelMap modelMap) {

        int status = WebRequestConstant.SUCCESS_STATUS;
        String message = WebRequestConstant.SUCCESS_MESSAGE;
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());

        T entity = null;

        try {
            status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, status);
            message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, message);

            if (0 == status) {
                final long id = RequestParamUtil.getRequestParamLong("id", request, modelMap, 0L);
                if (0L == id) {
                    status = 1;
                    message = "未传id，或者id无效！ ";
                } else {
                    IEntityService entityService = getEntityService();
                    if (null != entityService) {
                        entity = (T) entityService.fetchDetail(id);

                        String entityVOClassSimpleName = getEntityVOClassSimpleName();
                        result.put(entityVOClassSimpleName.substring(0, 1).toLowerCase().concat(entityVOClassSimpleName.substring(1)), entity);
                    } // if (null != entityService)                           
                }
            } // if (0 == status)
        } catch (Exception e) {
            e.printStackTrace();
            status = WebRequestConstant.EXCEPTION_STATUS;
            message = WebRequestConstant.EXCEPTION_MESSAGE;
            if (null != e.getLocalizedMessage()) {
                message += " " + e.getLocalizedMessage();
            } // if (null != e.getLocalizedMessage())
        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);

        return entity;
    } // handleFetchDetail

    protected void postFetchDetail(HttpServletRequest request, ModelMap modelMap) {

    } // postFetchDetail

    @RequestMapping(value = "/fetchDetail", method = { RequestMethod.GET, RequestMethod.POST })
    public void fetchDetail(HttpServletRequest request, ModelMap modelMap) {

        if (validateFetchDetail(request, modelMap)) {
            preFetchDetail(request, modelMap);
            handleFetchDetail(request, modelMap);
            postFetchDetail(request, modelMap);
        } // if (validateFetchDetail(request, modelMap))
    }
}
