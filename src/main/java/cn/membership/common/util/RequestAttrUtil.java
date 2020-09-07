/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.taglibs.standard.lang.jstl.NullLiteral;
import org.springframework.ui.ModelMap;

import cn.membership.web.constant.WebRequestConstant;

public class RequestAttrUtil {
    
    public static final <K, V> void setRequestAttrMap(String requestAttrKey, Map<K, V> requestAttrMap, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Map.class, requestAttrKey, requestAttrMap, request, modelMap);
    }
    
    public static final <K, V> Map<K, V> getRequestAttrMap(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrMap(requestAttrKey, request, modelMap, null);
    }

    public static final <K, V> Map<K, V> getRequestAttrMap(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Map<K, V> defaultValue) {
        return getRequestAttrObject(Map.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final <T> void setRequestAttrList(String requestAttrKey, List<T> requestAttrList, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(List.class, requestAttrKey, requestAttrList, request, modelMap);
    }

    public static final <T> List<T> getRequestAttrList(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrList(requestAttrKey, request, modelMap, null);
    }

    public static final <T> List<T> getRequestAttrList(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, List<T> defaultValue) {
        return getRequestAttrObject(List.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setRequestAttrDate(String requestAttrKey, Date requestAttrDate, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Date.class, requestAttrKey, requestAttrDate, request, modelMap);
    }

    public static final Date getRequestAttrDate(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrDate(requestAttrKey, request, modelMap, new Date());
    }

    public static final Date getRequestAttrDate(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Date defaultValue) {
        return getRequestAttrObject(Date.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setRequestAttrBoolean(String requestAttrKey, Boolean requestAttrBoolean, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Boolean.class, requestAttrKey, requestAttrBoolean, request, modelMap);
    }

    public static final Boolean getRequestAttrBoolean(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrBoolean(requestAttrKey, request, modelMap, false);
    }

    public static final Boolean getRequestAttrBoolean(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Boolean defaultValue) {
        return getRequestAttrObject(Boolean.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setRequestAttrLong(String requestAttrKey, Long requestAttrLong, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Long.class, requestAttrKey, requestAttrLong, request, modelMap);
    }

    public static final Long getRequestAttrLong(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrLong(requestAttrKey, request, modelMap, 0L);
    }

    public static final Long getRequestAttrLong(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Long defaultValue) {
        return getRequestAttrObject(Long.class, requestAttrKey, request, modelMap, defaultValue);
    }    
    
    public static final void setRequestAttrInteger(String requestAttrKey, Integer requestAttrInteger, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Integer.class, requestAttrKey, requestAttrInteger, request, modelMap);
    }

    public static final Integer getRequestAttrInteger(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrInteger(requestAttrKey, request, modelMap, 0);
    }

    public static final Integer getRequestAttrInteger(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Integer defaultValue) {
        return getRequestAttrObject(Integer.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setRequestAttrFloat(String requestAttrKey, Float requestAttrFloat, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Float.class, requestAttrKey, requestAttrFloat, request, modelMap);
    }

    public static final Float getRequestAttrFloat(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrFloat(requestAttrKey, request, modelMap, 0F);
    }

    public static final Float getRequestAttrFloat(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Float defaultValue) {
        return getRequestAttrObject(Float.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setRequestAttrDouble(String requestAttrKey, Double requestAttrDouble, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(Double.class, requestAttrKey, requestAttrDouble, request, modelMap);
    }

    public static final Double getRequestAttrDouble(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrDouble(requestAttrKey, request, modelMap, 0D);
    }

    public static final Double getRequestAttrDouble(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, Double defaultValue) {
        return getRequestAttrObject(Double.class, requestAttrKey, request, modelMap, defaultValue);
    }    
    
    public static final void setRequestAttrString(String requestAttrKey, String requestAttrString, HttpServletRequest request, ModelMap modelMap) {
        setRequestAttrObject(String.class, requestAttrKey, requestAttrString, request, modelMap);
    }

    public static final String getRequestAttrString(String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestAttrString(requestAttrKey, request, modelMap, null);
    }

    public static final String getRequestAttrString(String requestAttrKey, HttpServletRequest request, ModelMap modelMap, String defaultValue) {
        return getRequestAttrObject(String.class, requestAttrKey, request, modelMap, defaultValue);
    }
    
    public static final <T> void setRequestAttrObject(Class<T> type, String requestAttrKey, T requestAttrObject, HttpServletRequest request, ModelMap modelMap) {
        if (null != type && null != requestAttrKey && null != request) {
            request.setAttribute(requestAttrKey, requestAttrObject);
        } // if (null != type && null != requestAttrKey && null != request)
    }
    
    public static final <T> T getRequestAttrObject(Class<T> type, String requestAttrKey, HttpServletRequest request, ModelMap modelMap, T defaultValue) {
        T requestAttrValue = defaultValue;
        
        if (null != type && null != requestAttrKey && null != request) {
            Object requestAttrValueObject = request.getAttribute(requestAttrKey);
            if(null == requestAttrValueObject){
            	 requestAttrValueObject = modelMap.get(requestAttrKey);
            }
            if (null != requestAttrValueObject) {
                
                if (requestAttrValueObject.getClass() == type) {
                    requestAttrValue = (T) requestAttrValueObject;
                } else {
                    if (Boolean.class == type && requestAttrValueObject instanceof Integer) {
                        if (1 == (Integer) requestAttrValueObject) {
                            requestAttrValue = (T) new Boolean(true);
                        } else {
                            requestAttrValue = (T) new Boolean(false);
                        }
                    } else if (Map.class == type && requestAttrValueObject instanceof Map) {
                        requestAttrValue = (T) requestAttrValueObject;
                    } else if (List.class == type && requestAttrValueObject instanceof List) {
                        requestAttrValue = (T) requestAttrValueObject;
                    }
                }
            } 
//            else {
//                Object allRequestAttrMapObject = request.getAttribute(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY);
//                if (null == allRequestAttrMapObject || !(allRequestAttrMapObject instanceof Map)) {
//                    prepareRequestAttr(request, null, modelMap);
//                    requestAttrValue = getRequestAttrObject(type, requestAttrKey, request, modelMap, defaultValue);
//                } // if (null == allRequestAttrMapObject || !(allRequestAttrMapObject instanceof Map))
//
//            } // else
            
            if (null != requestAttrValue && null != WebRequestConstant.ARG_REQUEST_ATTR_KEY_LIST && WebRequestConstant.ARG_REQUEST_ATTR_KEY_LIST.contains(requestAttrKey)) {
                if (requestAttrValue instanceof ArrayList) {
                    requestAttrValue = (T) (new ArrayList((ArrayList)requestAttrValue));
                } else if (requestAttrValue instanceof LinkedList) {
                    requestAttrValue = (T) (new LinkedList((LinkedList)requestAttrValue));
                } else if (requestAttrValue instanceof HashMap) {
                    requestAttrValue = (T) (new HashMap((HashMap)requestAttrValue));
                } else if (requestAttrValue instanceof LinkedHashMap) {
                    requestAttrValue = (T) (new LinkedHashMap((LinkedHashMap)requestAttrValue));
                }
            } // if (null != requestAttrValue && null != WebRequestConstant.ARG_REQUEST_ATTR_KEY_LIST && WebRequestConstant.ARG_REQUEST_ATTR_KEY_LIST.contains(requestAttrKey)) 

            
        } // if (null != type && null != requestAttrKey && null != request)

        return requestAttrValue;
    }
    
    public static final <T> Boolean isRequestAttrObjectAvailable(Class<T> type, String requestAttrKey, HttpServletRequest request, ModelMap modelMap) {
        boolean isRequestAttrObjectAvailable = false;
        
        T requestAttrValue = getRequestAttrObject(type, requestAttrKey, request, modelMap, null);
        if (null != requestAttrValue) {
            isRequestAttrObjectAvailable = true;
        } // if (null != requestAttrValue)
        
        return isRequestAttrObjectAvailable;
    }
    
}
