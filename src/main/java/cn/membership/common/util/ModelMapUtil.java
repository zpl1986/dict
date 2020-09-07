/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

public class ModelMapUtil {
    
    public static final <K, V> void setModelMapMap(String modelMapAttrKey, Map<K, V> modelMapAttrMap, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Map.class, modelMapAttrKey, modelMapAttrMap, request, modelMap);
    }
    
    public static final <K, V> Map<K, V> getModelMapMap(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapMap(modelMapAttrKey, request, modelMap, null);
    }
    
    public static final <K, V> Map<K, V> getModelMapMap(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Map<K, V> defaultValue) {
        return getModelMapObject(Map.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final <T> void setModelMapList(String modelMapAttrKey, List<T> modelMapAttrList, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(List.class, modelMapAttrKey, modelMapAttrList, request, modelMap);
    }
    
    public static final <T> List<T> getModelMapList(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapList(modelMapAttrKey, request, modelMap, null);
    }
    
    public static final <T> List<T> getModelMapList(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, List<T> defaultValue) {
        return getModelMapObject(List.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapDate(String modelMapAttrKey, Date modelMapAttrDate, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Date.class, modelMapAttrKey, modelMapAttrDate, request, modelMap);
    }
    
    public static final Date getModelMapDate(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapDate(modelMapAttrKey, request, modelMap, new Date());
    }
    
    public static final Date getModelMapDate(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Date defaultValue) {
        return getModelMapObject(Date.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapBoolean(String modelMapAttrKey, Boolean modelMapAttrBoolean, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Boolean.class, modelMapAttrKey, modelMapAttrBoolean, request, modelMap);
    }

    public static final Boolean getModelMapBoolean(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapBoolean(modelMapAttrKey, request, modelMap, false);
    }
    
    public static final Boolean getModelMapBoolean(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Boolean defaultValue) {
        return getModelMapObject(Boolean.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapFloat(String modelMapAttrKey, Float modelMapAttrFloat, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Float.class, modelMapAttrKey, modelMapAttrFloat, request, modelMap);
    }
    
    public static final Float getModelMapFloat(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapFloat(modelMapAttrKey, request, modelMap, 0F);
    }
    
    public static final Float getModelMapFloat(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Float defaultValue) {
        return getModelMapObject(Float.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapDouble(String modelMapAttrKey, Double modelMapAttrDouble, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Double.class, modelMapAttrKey, modelMapAttrDouble, request, modelMap);
    }
    
    public static final void setModelMapBigDecimal(String modelMapAttrKey, BigDecimal modelMapAttrDouble, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(BigDecimal.class, modelMapAttrKey, modelMapAttrDouble, request, modelMap);
    }
    
    public static final Double getModelMapDouble(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapDouble(modelMapAttrKey, request, modelMap, 0D);
    }
    
    public static final Double getModelMapDouble(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Double defaultValue) {
        return getModelMapObject(Double.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapLong(String modelMapAttrKey, Long modelMapAttrLong, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Long.class, modelMapAttrKey, modelMapAttrLong, request, modelMap);
    }
    
    public static final Long getModelMapLong(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapLong(modelMapAttrKey, request, modelMap, 0L);
    }
    
    public static final Long getModelMapLong(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Long defaultValue) {
        return getModelMapObject(Long.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapInteger(String modelMapAttrKey, Integer modelMapAttrInteger, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(Integer.class, modelMapAttrKey, modelMapAttrInteger, request, modelMap);
    }

    public static final Integer getModelMapInteger(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapInteger(modelMapAttrKey, request, modelMap, 0);
    }
    
    public static final Integer getModelMapInteger(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, Integer defaultValue) {
        return getModelMapObject(Integer.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final void setModelMapString(String modelMapAttrKey, String modelMapAttrString, HttpServletRequest request, ModelMap modelMap) {
        setModelMapObject(String.class, modelMapAttrKey, modelMapAttrString, request, modelMap);
    }
    
    public static final String getModelMapString(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        return getModelMapString(modelMapAttrKey, request, modelMap, null);
    }
    
    public static final String getModelMapString(String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, String defaultValue) {
        return getModelMapObject(String.class, modelMapAttrKey, request, modelMap, defaultValue);
    }
    
    public static final <T> void setModelMapObject(Class<T> type, String modelMapAttrKey, T modelMapAttrObject, HttpServletRequest request, ModelMap modelMap) {
        if (null != type && null != modelMapAttrKey) {
            
            if (null != modelMap) {
                modelMap.addAttribute(modelMapAttrKey, modelMapAttrObject);
            } else if (null != request) {
                request.setAttribute(modelMapAttrKey, modelMapAttrObject);
            }
            
        } // if (null != type && null != modelMapAttrKey)
    }

    public static final <T> T getModelMapObject(Class<T> type, String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap, T defaultValue) {
        T modelMapAttrValue = defaultValue;
        
        if (null != type && null != modelMapAttrKey) {
            Object modelMapAttrValueObject = null;
            
            if (null != modelMap) {
                modelMapAttrValueObject = modelMap.get(modelMapAttrKey);
            } // if (null != modelMap)
            
            if (null == modelMapAttrValueObject && null != request) {
                modelMapAttrValueObject = request.getAttribute(modelMapAttrKey);
                if (null != modelMapAttrValueObject && null != modelMap) {
                    modelMap.addAttribute(modelMapAttrKey, modelMapAttrValueObject);
                } // if (null != modelMapAttrValueObject && null != modelMap)
            } // if (null == modelMapAttrValueObject && null != request)
            
            if (null != modelMapAttrValueObject) {
                if (modelMapAttrValueObject.getClass() == type) {
                    modelMapAttrValue = (T) modelMapAttrValueObject;
                } else {
                    if (Boolean.class == type && modelMapAttrValueObject instanceof Integer) {
                        if (1 == (Integer) modelMapAttrValueObject) {
                            modelMapAttrValue = (T) new Boolean(true);
                        } else {
                            modelMapAttrValue = (T) new Boolean(false);
                        }
                    } else if (Map.class == type && modelMapAttrValueObject instanceof Map) {
                        modelMapAttrValue = (T) modelMapAttrValueObject;
                    } else if (List.class == type && modelMapAttrValueObject instanceof List) {
                        modelMapAttrValue = (T) modelMapAttrValueObject;
                    }
                }
            } // if (null != modelMapAttrValueObject)
        } // if (null != type && null != modelMapAttrKey)

        return modelMapAttrValue;
    }
    
    public static final <T> Boolean isModelMapObjectAvailable(Class<T> type, String modelMapAttrKey, HttpServletRequest request, ModelMap modelMap) {
        boolean isModelMapObjectAvailable = false;
        
        T modelMapAttrValue = getModelMapObject(type, modelMapAttrKey, request, modelMap, null);
        if (null != modelMapAttrValue) {
            isModelMapObjectAvailable = true;
        } // if (null != modelMapAttrValue)
        
        return isModelMapObjectAvailable;
    }

}
