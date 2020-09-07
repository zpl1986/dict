/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao;

import java.util.Map;

public interface IShortMessageDao {

    public void insert(Map<String, Object> valueArgMap);

    public int todaySendCount(String mobile);

}
