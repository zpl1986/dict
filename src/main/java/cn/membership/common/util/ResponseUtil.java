package cn.membership.common.util;


public class ResponseUtil {
	public static final int PSW_MIN_LENGTH = 4;
	public static final int PSW_MAX_LENGTH = 45;
	
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_ERROR = -1;
	public static final int STATUS_AUTH_FAILTURE = -2;
	public static final String MSG_AUTH_FAILTURE = "非法访问！";
	public static final String MSG_SUCCESS = "成功";
	public static final String MSG_ERROR = "失败";
	public static final String MSG_TEL_NG = "请输入11位手机号码";
	public static final String MSG_PSW_NG = "请输入密码，并且长度最小"+PSW_MIN_LENGTH+"位,最大"+PSW_MAX_LENGTH+"位";
	public static final String MSG_PSW_INCORRECT = "密码不正确";
	public static final String MSG_USERNAME_HAS_BEEN_REGISTERED = "用户名已被注册";
	public static final String MSG_UNKNOW_ERROR = "未知错误";

	public static final String REDIRECT_TO_JSON_OUTPUT = "redirect:/json/output";
}
