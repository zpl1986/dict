/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.membership.web.dao.IWordDao;
import cn.membership.web.dao.impl.WordDao;
import cn.membership.web.service.IWordService;
import cn.membership.web.vo.Word;

/**
* 服务
*
* @author 
* @version 0.0.1
* @since
*/
@Service
@Qualifier("wordService")
public class WordService extends EntityService<Word,WordDao> implements IWordService {

@Autowired
@Qualifier("wordDao")
private IWordDao wordDao;


}