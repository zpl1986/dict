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

import cn.membership.web.constant.WebRequestConstant;

public class RequestParamUtil {

    public static final <T> List<T> getRequestParamList(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamList(requestParamKey, request, modelMap, null);
    }

    public static final <T> List<T> getRequestParamList(String requestParamKey, HttpServletRequest request, ModelMap modelMap, List<T> defaultValue) {
        return getRequestParamObject(List.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final Date getRequestParamDate(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamDate(requestParamKey, request, modelMap, null);
    }

    public static final Date getRequestParamDate(String requestParamKey, HttpServletRequest request, ModelMap modelMap, Date defaultValue) {
        return getRequestParamObject(Date.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final Boolean getRequestParamBoolean(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamBoolean(requestParamKey, request, modelMap, false);
    }

    public static final Boolean getRequestParamBoolean(String requestParamKey, HttpServletRequest request, ModelMap modelMap, Boolean defaultValue) {
        return getRequestParamObject(Boolean.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final Double getRequestParamDouble(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamDouble(requestParamKey, request, modelMap, 0D);
    }

    public static final Double getRequestParamDouble(String requestParamKey, HttpServletRequest request, ModelMap modelMap, Double defaultValue) {
        return getRequestParamObject(Double.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final Float getRequestParamFloat(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamFloat(requestParamKey, request, modelMap, 0F);
    }

    public static final Float getRequestParamFloat(String requestParamKey, HttpServletRequest request, ModelMap modelMap, Float defaultValue) {
        return getRequestParamObject(Float.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final Long getRequestParamLong(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamLong(requestParamKey, request, modelMap, 0L);
    }

    public static final Long getRequestParamLong(String requestParamKey, HttpServletRequest request, ModelMap modelMap, Long defaultValue) {
        return getRequestParamObject(Long.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final Integer getRequestParamInteger(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamInteger(requestParamKey, request, modelMap, 0);
    }

    public static final Integer getRequestParamInteger(String requestParamKey, HttpServletRequest request, ModelMap modelMap, Integer defaultValue) {
        return getRequestParamObject(Integer.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final String getRequestParamString(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamString(requestParamKey, request, modelMap, null);
    }

    public static final String getRequestParamString(String requestParamKey, HttpServletRequest request, ModelMap modelMap, String defaultValue) {
        return getRequestParamObject(String.class, requestParamKey, request, modelMap, defaultValue);
    }

    public static final <T> T getRequestParamObject(Class<T> type, String requestParamKey, HttpServletRequest request, ModelMap modelMap, T defaultValue) {
        T requestParamValue = defaultValue;

        if (null != type && null != requestParamKey && null != request) {
            Map<String, Object> requestParams = getRequestParams(request, modelMap);
            if (null != requestParams) {
                Object requestParamValueObject = requestParams.get(requestParamKey);
                if (null != requestParamValueObject) {
                    if (requestParamValueObject.getClass() == type) {
                        requestParamValue = (T) requestParamValueObject;
                    } else if (Integer.class == type) {
                        try {
                            requestParamValue = (T) new Integer(requestParamValueObject.toString());
                        } catch (Exception e) {
                        }
                    } else if (BigDecimal.class == type) {
                        try {
                            requestParamValue = (T) new BigDecimal(requestParamValueObject.toString());
                        } catch (Exception e) {
                        }
                    } else if (Long.class == type) {
                        try {
                            requestParamValue = (T) new Long(requestParamValueObject.toString());
                        } catch (Exception e) {
                        }
                    } else {
                        if (Boolean.class == type && requestParamValueObject instanceof Integer) {
                            if (1 == (Integer) requestParamValueObject) {
                                requestParamValue = (T) new Boolean(true);
                            } else {
                                requestParamValue = (T) new Boolean(false);
                            }
                        } else if (Map.class == type && requestParamValueObject instanceof Map) {
                            requestParamValue = (T) requestParamValueObject;
                        } else if (List.class == type && requestParamValueObject instanceof List) {
                            requestParamValue = (T) requestParamValueObject;
                        }
                    }

                } // if (null != requestParamValueObject)
            } // if (null != requestParams)
        } // if (null != type && null != requestParamKey && null != request)

        return requestParamValue;
    }

    public static final <T> Boolean isRequestParamObjectAvailable(Class<T> type, String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        boolean isRequestParamObjectAvailable = false;

        T requestParamValue = getRequestParamObject(type, requestParamKey, request, modelMap, null);
        if (null != requestParamValue) {
            isRequestParamObjectAvailable = true;
        } // if (null != requestParamValue)

        return isRequestParamObjectAvailable;
    }

    public static final Map<String, Object> getRequestArgs(HttpServletRequest request, ModelMap modelMap) {
        return RequestAttrUtil.getRequestAttrMap(WebRequestConstant.REQUEST_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
    }

    public static final Map<String, Object> getRequestParams(HttpServletRequest request, ModelMap modelMap) {
        return RequestAttrUtil.getRequestAttrMap(WebRequestConstant.ALL_ARG_MAP_REQUEST_ATTR_KEY, request, modelMap);
    }

    public static final String getRequestUrl(HttpServletRequest request, ModelMap modelMap) {
        String requestUrl = null;
        if (null != request && null != request.getRequestURL()) {
            requestUrl = request.getRequestURL().toString();
        }

        return requestUrl;
    }

    public static final String getRequestBaseUrl(HttpServletRequest request, ModelMap modelMap) {
        String requestBaseUrl = null;

        StringBuffer requestBaseUrlStrBuf = new StringBuffer();
        if (null != request) {
            if (request.getRemoteHost().startsWith("10.")) { //阿里云 TODO
                requestBaseUrlStrBuf.append("https");
                requestBaseUrlStrBuf.append("://");
                requestBaseUrlStrBuf.append(request.getServerName());
                requestBaseUrlStrBuf.append(request.getContextPath());
                requestBaseUrlStrBuf.append("/");

                requestBaseUrl = requestBaseUrlStrBuf.toString();

            } else {
                requestBaseUrlStrBuf.append(request.getScheme());
                requestBaseUrlStrBuf.append("://");
                requestBaseUrlStrBuf.append(request.getServerName());
                requestBaseUrlStrBuf.append(":");
                requestBaseUrlStrBuf.append(request.getServerPort());
                requestBaseUrlStrBuf.append(request.getContextPath());
                requestBaseUrlStrBuf.append("/");

                requestBaseUrl = requestBaseUrlStrBuf.toString();
            }
        } // if (null != request)

        return requestBaseUrl;
    }

    public static final String getRequestQueryString(HttpServletRequest request, ModelMap modelMap) {
        String requestQueryString = null;

        if (null != request && null != request.getQueryString()) {
            requestQueryString = request.getQueryString();
        } // if (null != request && null != request.getQueryString())

        return requestQueryString;
    }

    public static final BigDecimal getRequestParamBigDecimal(String requestParamKey, HttpServletRequest request, ModelMap modelMap) {
        return getRequestParamBigDecimal(requestParamKey, request, modelMap, BigDecimal.ZERO);
    }

    public static final BigDecimal getRequestParamBigDecimal(String requestParamKey, HttpServletRequest request, ModelMap modelMap, BigDecimal defaultValue) {
        return getRequestParamObject(BigDecimal.class, requestParamKey, request, modelMap, defaultValue);
    }
}
