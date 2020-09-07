/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import cn.membership.web.dao.IShortMessageDao;

@Repository
@Qualifier("shortMessageDao")
public class ShortMessageDao extends BaseDao implements IShortMessageDao {

    @Override
    public void insert(Map<String, Object> valueArgMap) {
        String table = "short_message";
        try {
            insert(table, valueArgMap);
        } catch (Exception e) {
            if (e.getMessage().contains("doesn't exist")) {
                try {
                    String sql = "CREATE TABLE `" + table + "` (  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标识',  `mobile` varchar(11) NOT NULL COMMENT '手机号码',  `content` varchar(255) DEFAULT NULL COMMENT '短信内容',  `result_code` int(11) DEFAULT NULL COMMENT '发送结果代码',  `result_message` varchar(32) NOT NULL COMMENT '发送结果消息',  `send_message_switch` int(11) NOT NULL DEFAULT '0' COMMENT '发送开关，1发送，0不发送',  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信记录';";
                    namedParameterJdbcTemplate.getJdbcOperations().update(sql);
                    insert(table, valueArgMap);
                } catch (DataAccessException e1) {
                    log.error(e1);
                } catch (Exception e2) {
                    log.error(e2);
                }
            }

        }
    }

    /**
     * 今天发送的验证码短信条数
     * @param mobile
     */
    @Override
    public int todaySendCount(String mobile) {
        int count = 0;
        
        try {
            String sql = "SELECT count(*) c  from short_message WHERE created_at > :today AND mobile = :mobile AND result_code = 0 AND content LIKE '%验证码%';";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("mobile", mobile);
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd000000");
            String today = simpleDateFormat.format(new Date());
            paramMap.put("today", today);
            count = queryForInt(sql, paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

}
