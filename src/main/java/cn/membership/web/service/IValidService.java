/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.service;

import java.lang.reflect.Type;
import java.util.Map;

public interface IValidService {

    public Map<String, Object> valid(Map<String, Object> parameterMap, Type type);

}
