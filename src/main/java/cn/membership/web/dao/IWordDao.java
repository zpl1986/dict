/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.dao;

import cn.membership.web.vo.Word;

/**
 * DAO接口
 *
 * @author
 * @version 0.0.1
 * @since
 */
public interface IWordDao extends IEntityDao<Word> {

	Word findById(String key);

}