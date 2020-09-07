
package cn.membership.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.membership.web.service.impl.WordService;
import cn.membership.web.vo.Word;

/**
 * 版块控制器
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since
 */
@Controller
@RequestMapping("/t")
public class TestController extends EntityController<Word, WordService> {

	@Autowired
	@Qualifier("namedParameterJdbcTemplate")
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@RequestMapping(value = "/test", method = { RequestMethod.GET, RequestMethod.POST })
	public String test(HttpServletRequest request, ModelMap modelMap) {
		
		return "upload";
	}

//	@RequestMapping(value = "/updateSummary", method = { RequestMethod.GET, RequestMethod.POST })
//	public void updateSummary(HttpServletRequest request, ModelMap modelMap) {
//
//	}

}
