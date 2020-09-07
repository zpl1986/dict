 
package cn.membership.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.membership.common.util.ModelMapUtil;
import cn.membership.common.util.RequestParamUtil;
import cn.membership.common.util.Validator;
import cn.membership.web.constant.WebRequestConstant;
import cn.membership.web.service.IEmailService;

@Controller
@RequestMapping("/util")
public class EmailController {

    @Autowired
    @Qualifier("emailService")
    private IEmailService emailService;

    @RequestMapping(value = "/checkMail", method = { RequestMethod.GET, RequestMethod.POST })
    public void checkMail(HttpServletRequest request, ModelMap modelMap) {
        String to = request.getParameter("email");
        emailService.sendMail("380007905xx@qq.com", "test", "haha");
    }

    private static String MOBILE_REQUEST_PARAM_KEY = "mobile";
    private static String IDENTIFYING_CODE_PARAM_KEY = "identifyingCode";

    @RequestMapping(value = "/identifyingCode", method = { RequestMethod.GET, RequestMethod.POST })
    public void identifyingCode(HttpServletRequest request, ModelMap modelMap) {

        int status = ModelMapUtil.getModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, request, modelMap, WebRequestConstant.SUCCESS_STATUS);
        String message = ModelMapUtil.getModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, request, modelMap, WebRequestConstant.SUCCESS_MESSAGE);
        Map<String, Object> result = ModelMapUtil.getModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, request, modelMap, new HashMap<String, Object>());

        String mobile = RequestParamUtil.getRequestParamString(MOBILE_REQUEST_PARAM_KEY, request, modelMap);

        if (StringUtils.isEmpty(mobile)) {
            status = 1;
            message = "手机号码'mobile'必填";
        } else if (!Validator.isMobileOK(mobile)) {
            status = 1;
            message = "手机号码格式不正确";
        } else {

        }

        ModelMapUtil.setModelMapInteger(WebRequestConstant.STATUS_MODEL_MAP_KEY, status, request, modelMap);
        ModelMapUtil.setModelMapString(WebRequestConstant.MESSAGE_MODEL_MAP_KEY, message, request, modelMap);
        ModelMapUtil.setModelMapMap(WebRequestConstant.RESULT_MODEL_MAP_KEY, result, request, modelMap);
    }

}
