
package cn.membership.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;

import cn.membership.common.util.ModelMapUtil;
import cn.membership.web.constant.WebRequestConstant;
import cn.membership.web.dao.IWordDao;
import cn.membership.web.service.IEmailService;
import cn.membership.web.service.IWordService;
import cn.membership.web.service.impl.WordService;
import cn.membership.web.vo.Word;

/**
 * 用户控制器
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since
 * 
 */
@Controller
@RequestMapping("/api")
public class DictController extends EntityController<Word, WordService> {

	@Autowired
	@Qualifier("emailService")
	private IEmailService emailService;
	@Autowired
	@Qualifier("wordService")
	private IWordService wordService;
	@Autowired
	@Qualifier("wordDao")
	private IWordDao wordDao;

	@RequestMapping(value = "/text", method = { RequestMethod.GET, RequestMethod.POST })
	public void register(HttpServletRequest request, ModelMap modelMap) {
		String parameter = request.getParameter("content");
		String[] split = parameter.split("\\W");
		HashSet<String> hashSet = new HashSet<String>(Arrays.asList(split));
		Map<String, Object> result = new HashMap<String, Object>();
		String[] level = request.getParameterValues("level");
		System.out.println(JSON.toJSONString(level));
		for (String key : hashSet) {
			Word word = wordDao.findById(key);
			
			result.put(key, word.getContent());
		}

		ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);
	}

	@RequestMapping(value = "/upload", method = { RequestMethod.GET, RequestMethod.POST })
	public void delete(HttpServletRequest request, ModelMap modelMap) {
//		String operatorId = request.getParameter("operatorId");
//		String operatorToken = request.getParameter("operatorToken");
//		Map<String, Object> whereArgMap = new HashMap<String, Object>();
//		whereArgMap.put("id", operatorId);
//		whereArgMap.put("token", operatorToken);
//		List<User> fetch = userService.fetch(0, 1, whereArgMap);
//
//		if (fetch == null || fetch.size() == 0) {
//			ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, 1, request, modelMap);
//			ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, "您无权限操作", request, modelMap);
//			return;
//		}
//		User user = fetch.get(0);
//		if (user.getUserType() == 1) {
//
//		} else if (user.getUserType() == 2) {
//			whereArgMap.clear();
//			whereArgMap.put("userId", operatorId);
//
//		} else {
//			ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, 1, request, modelMap);
//			ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, "您无权限操作", request, modelMap);
//			return;
//		}
//
//		int status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap,
//				WebRequestConstant.SUCCESS_STATUS);
//		String message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap,
//				WebRequestConstant.SUCCESS_MESSAGE);
//		Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request,
//				modelMap, new HashMap<String, Object>());
//		String mobile = request.getParameter("mobile");
//
//		if (StringUtils.isEmpty(mobile)) {
//			status = 1;
//			message = "mobile必填";
//		} else {
//			whereArgMap.clear();
//			whereArgMap.put("mobile", mobile);
//			List<User> users = userService.fetch(0, 1, whereArgMap);
//			if (null != users && users.size() > 0) {
//				Integer userType = users.get(0).getUserType();
//				if (userType != 2) {
//					status = 1;
//					message = "此用户不是操作员";
//				} else {
//				}
//			} else {
//				status = 1;
//				message = "没有此用户：" + mobile;
//			}
//
//		}
//		ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
//		ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
//		ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);
	}

}
