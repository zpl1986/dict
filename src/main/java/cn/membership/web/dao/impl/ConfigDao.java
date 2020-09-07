/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.membership.web.dao.IConfigDao;
@Repository
@Qualifier("configDao")
public class ConfigDao implements IConfigDao {
	
    protected final Logger log = Logger.getLogger(getClass());
    
    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean isSendMessage() {
        boolean send = true;
        String sql = "SELECT value FROM config_detail WHERE config_category_code = 'SEND_MESSAGE_SWITCH';";
        Map<String, Object> paramMap = new HashMap<String, Object>();

        try {
            Integer sendMessageSwitch = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
            Integer notSend = 0;//0 表示不发送
            if (notSend.equals(sendMessageSwitch)) {
                send = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return send;
    }
    
    
    public String platformInfo(String code) {
        String value = null;
        String sql = "SELECT value FROM config_detail WHERE config_category_code = 'PLATFORM_INFO'  AND code = :code LIMIT 1;";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("code", code);
        try {
            value = namedParameterJdbcTemplate.queryForObject(sql, paramMap, String.class);
        } catch (Exception e) {
            log.warn("没有配置PLATFORM_INFO相关信息" + code);
        }
        return value;
    }
    
}
